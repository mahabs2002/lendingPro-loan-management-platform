package com.loanapp.account;

import com.loanapp.account.dto.LoanAccountResponse;
import com.loanapp.entity.*;
import com.loanapp.enums.LoanStatus;
import com.loanapp.exception.ResourceNotFoundException;
import com.loanapp.notification.NotificationService;
import com.loanapp.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LoanAccountService {

    private final LoanAccountRepository repository;
    private final NotificationService notificationService;

    public LoanAccount createAccount(LoanApplication application,
                                     java.math.BigDecimal approvedAmount,
                                     Integer approvedTenure) {
        LoanAccount account = LoanAccount.builder()
                .application(application)
                .loanAccountNumber(generateAccountNumber())
                .approvedAmount(approvedAmount)
                .interestRate(application.getProduct().getInterestRate())
                .tenureMonths(approvedTenure != null ? approvedTenure : application.getRequestedTenure())
                .disbursedDate(LocalDate.now())
                .outstandingBalance(approvedAmount)
                .loanStatus(LoanStatus.ACTIVE)
                .build();

        LoanAccount saved = repository.save(account);

        notificationService.send(
            application.getUser().getId(),
            NotificationType.LOAN_APPROVED,
            "Your loan of Rs. " + approvedAmount + " has been approved. Account: " + saved.getLoanAccountNumber()
        );

        return saved;
    }

    public LoanAccountResponse getById(Long id) {
//        return repository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Loan account not found: " + id));

        LoanAccount account=repository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Loan account not found: " + id));
        return mapToDto(account);
    }

    public LoanAccount getByAccountNumber(String number) {
        return repository.findByLoanAccountNumber(number)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + number));
    }

    public List<LoanAccountResponse> getActiveLoans() {
      return repository.findByLoanStatus(LoanStatus.ACTIVE)
        .stream().map(this::mapToDto)
        .toList();
    }

    public List<LoanAccountResponse> getMyLoans(Long userId) {
        return repository.findByApplicationUserId(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public void updateBalance(LoanAccount account, java.math.BigDecimal newBalance) {
        account.setOutstandingBalance(newBalance);
        if (newBalance.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            account.setLoanStatus(LoanStatus.CLOSED);
        }
        repository.save(account);
    }

    private String generateAccountNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int rand = new Random().nextInt(99999);
        return "LN" + date + String.format("%05d", rand);
    }

    private LoanAccountResponse mapToDto(LoanAccount acc) {
        return LoanAccountResponse.builder()
                .id(acc.getId())
                .loanAccountNumber(acc.getLoanAccountNumber())
                .approvedAmount(acc.getApprovedAmount())
                .interestRate(acc.getInterestRate())
                .tenureMonths(acc.getTenureMonths())
                .disbursedDate(acc.getDisbursedDate())
                .outstandingBalance(acc.getOutstandingBalance())
                .loanStatus(acc.getLoanStatus().name())

                // from application
                .applicationId(acc.getApplication().getId())
                .productName(acc.getApplication().getProduct().getProductName())

                .build();
    }
}
