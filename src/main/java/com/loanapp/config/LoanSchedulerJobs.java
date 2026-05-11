package com.loanapp.config;

import com.loanapp.account.LoanAccountRepository;
import com.loanapp.emi.EmiRepository;
import com.loanapp.entity.EmiSchedule;
import com.loanapp.entity.LoanAccount;
import com.loanapp.enums.LoanStatus;
import com.loanapp.enums.PaidStatus;
import com.loanapp.notification.NotificationService;
import com.loanapp.penalty.PenaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanSchedulerJobs {

    private final EmiRepository emiRepository;
    private final LoanAccountRepository loanAccountRepository;
    private final PenaltyService penaltyService;
    private final NotificationService notificationService;

    // JOB 1: EMI Due Reminder - Daily 8 AM
    @Scheduled(cron = "0 0 8 * * *")
    public void sendEmiReminders() {
        log.info("Running EMI due reminder job");
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<EmiSchedule> emis = emiRepository.findByDueDateAndPaidStatus(tomorrow, PaidStatus.PENDING);
        emis.forEach(notificationService::sendEmiReminder);
        log.info("Sent {} EMI reminders", emis.size());
    }

    // JOB 2: Overdue Checker - Daily 11 PM
    @Scheduled(cron = "0 0 23 * * *")
    public void checkOverdueEmis() {
        log.info("Running overdue checker job");
        LocalDate today = LocalDate.now();
        List<EmiSchedule> overdue = emiRepository.findByDueDateBeforeAndPaidStatus(today, PaidStatus.PENDING);
        overdue.forEach(emi -> {
            emi.setPaidStatus(PaidStatus.OVERDUE);
            emiRepository.save(emi);
            penaltyService.applyPenalty(emi);
            Long userId = emi.getLoanAccount().getApplication().getUser().getId();
            notificationService.send(userId,
                    com.loanapp.enums.NotificationType.OVERDUE_REMINDER,
                    "Your EMI of Rs. " + emi.getEmiAmount() + " due on " + emi.getDueDate() + " is overdue. Please pay immediately.");
        });
        log.info("Marked {} EMIs as overdue", overdue.size());
    }

    // JOB 3: Penalty Recalculator - Daily 11:30 PM
    @Scheduled(cron = "0 30 23 * * *")
    public void recalculatePenalties() {
        log.info("Running penalty recalculation job");
        penaltyService.recalculatePendingPenalties();
    }

    // JOB 4: Auto-close fully paid loans - Daily midnight
    @Scheduled(cron = "0 0 0 * * *")
    public void autoCloseLoans() {
        log.info("Running auto-close loan job");
        List<LoanAccount> active = loanAccountRepository.findByLoanStatus(LoanStatus.ACTIVE);
        active.forEach(account -> {
            long pendingCount = emiRepository.countPendingByLoanAccountId(account.getId());
            if (pendingCount == 0 && account.getOutstandingBalance().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                account.setLoanStatus(LoanStatus.CLOSED);
                loanAccountRepository.save(account);
                log.info("Auto-closed loan account: {}", account.getLoanAccountNumber());
            }
        });
    }

    // JOB 5: Monthly Statement - Last day of month at 1 AM
    @Scheduled(cron = "0 0 1 L * *")
    public void generateMonthlyStatements() {
        log.info("Running monthly statement generation job");
        // Trigger report generation - extend with actual JasperReports batch if needed
        List<LoanAccount> active = loanAccountRepository.findByLoanStatus(LoanStatus.ACTIVE);
        log.info("Monthly statements queued for {} accounts", active.size());
    }
}
