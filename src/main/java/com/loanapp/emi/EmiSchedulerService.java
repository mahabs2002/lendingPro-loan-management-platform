package com.loanapp.emi;

import com.loanapp.entity.*;
import com.loanapp.enums.PaidStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmiSchedulerService {

    private final EmiRepository emiRepository;

    public void generateSchedule(LoanAccount account) {
        BigDecimal principal = account.getApprovedAmount();
        double annualRate = account.getInterestRate().doubleValue();
        int months = account.getTenureMonths();

        double r = annualRate / (12.0 * 100.0);
        double emiVal;

        if (r == 0) {
            emiVal = principal.doubleValue() / months;
        } else {
            emiVal = (principal.doubleValue() * r * Math.pow(1 + r, months))
                    / (Math.pow(1 + r, months) - 1);
        }

        BigDecimal emiAmount = BigDecimal.valueOf(emiVal).setScale(2, RoundingMode.HALF_UP);
        BigDecimal balance = principal;
        LocalDate dueDate = account.getDisbursedDate().plusMonths(1);

        for (int i = 1; i <= months; i++) {
            BigDecimal interest = balance
                    .multiply(BigDecimal.valueOf(annualRate / (12.0 * 100.0)))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal principalPart = (i == months)
                    ? balance
                    : emiAmount.subtract(interest).setScale(2, RoundingMode.HALF_UP);

            balance = balance.subtract(principalPart).setScale(2, RoundingMode.HALF_UP);
            if (balance.compareTo(BigDecimal.ZERO) < 0) balance = BigDecimal.ZERO;

            EmiSchedule emi = EmiSchedule.builder()
                    .loanAccount(account)
                    .installmentNo(i)
                    .dueDate(dueDate)
                    .emiAmount(emiAmount)
                    .principalAmount(principalPart)
                    .interestAmount(interest)
                    .paidStatus(PaidStatus.PENDING)
                    .build();

            emiRepository.save(emi);
            dueDate = dueDate.plusMonths(1);
        }
        log.info("EMI schedule generated for loan account: {}", account.getLoanAccountNumber());
    }

    public List<EmiScheduleResponse> getSchedule(Long loanAccountId) {
        return emiRepository.findByLoanAccountIdOrderByInstallmentNo(loanAccountId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public EmiScheduleResponse getNextDue(Long loanAccountId) {
        EmiSchedule emi= emiRepository.findFirstByLoanAccountIdAndPaidStatusOrderByInstallmentNo(
                loanAccountId, PaidStatus.PENDING).orElse(null);
        return mapToDto(emi);
    }

    private EmiScheduleResponse mapToDto(EmiSchedule emi) {
        return EmiScheduleResponse.builder()
                .id(emi.getId())
                .installmentNo(emi.getInstallmentNo())
                .dueDate(emi.getDueDate())
                .emiAmount(emi.getEmiAmount())
                .principalAmount(emi.getPrincipalAmount())
                .interestAmount(emi.getInterestAmount())
                .penaltyAmount(emi.getPenaltyAmount())
                .paidStatus(emi.getPaidStatus().name())
                .paidDate(emi.getPaidDate())

                .loanAccountNumber(emi.getLoanAccount().getLoanAccountNumber())
                .build();
    }
}
