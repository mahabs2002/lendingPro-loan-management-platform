package com.loanapp.repayment;

import com.loanapp.account.LoanAccountRepository;
import com.loanapp.audit.AuditLogService;
import com.loanapp.emi.EmiRepository;
import com.loanapp.entity.*;
import com.loanapp.enums.PaidStatus;
import com.loanapp.enums.PaymentStatus;
import com.loanapp.exception.*;
import com.loanapp.notification.NotificationService;
import com.loanapp.enums.NotificationType;
import com.loanapp.repayment.dto.PaymentRequest;
import com.loanapp.repayment.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RepaymentService {

    private final PaymentRepository paymentRepository;
    private final LoanAccountRepository loanAccountRepository;
    private final EmiRepository emiRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Transactional
    public PaymentResponse makePayment(Long userId, PaymentRequest req) {
        LoanAccount account = loanAccountRepository.findById(req.getLoanAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Loan account not found"));

        EmiSchedule emi = null;
        if (req.getEmiId() != null) {
            emi = emiRepository.findById(req.getEmiId())
                    .orElseThrow(() -> new ResourceNotFoundException("EMI not found"));
        } else {
            emi = emiRepository.findFirstByLoanAccountIdAndPaidStatusOrderByInstallmentNo(
                    account.getId(), PaidStatus.PENDING).orElse(null);
        }

        Payment payment = Payment.builder()
                .loanAccount(account)
                .emiSchedule(emi)
                .paymentReference("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .amountPaid(req.getAmountPaid())
                .paymentMode(req.getPaymentMode())
                .paymentStatus(PaymentStatus.SUCCESS)
                .build();

        paymentRepository.save(payment);

        // Update EMI status
        if (emi != null) {
            BigDecimal emiTotal = emi.getEmiAmount().add(emi.getPenaltyAmount());
            if (req.getAmountPaid().compareTo(emiTotal) >= 0) {
                emi.setPaidStatus(PaidStatus.PAID);
                emi.setPaidDate(LocalDate.now());
            } else {
                emi.setPaidStatus(PaidStatus.PARTIAL);
            }
            emiRepository.save(emi);
        }

        // Update outstanding balance
        BigDecimal newBalance = account.getOutstandingBalance().subtract(req.getAmountPaid());
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) newBalance = BigDecimal.ZERO;
        account.setOutstandingBalance(newBalance);
        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
            account.setLoanStatus(com.loanapp.enums.LoanStatus.CLOSED);
        }
        loanAccountRepository.save(account);

        notificationService.send(userId, NotificationType.PAYMENT_SUCCESS,
                "Payment of Rs. " + req.getAmountPaid() + " received. Ref: " + payment.getPaymentReference());

        auditLogService.log(userId, "PAYMENT", "Payment", payment.getId(),
                null, "Amount: " + req.getAmountPaid(), null);

        return mapToDto(payment);
    }

    public List<PaymentResponse> getPaymentHistory(Long loanAccountId) {
        return paymentRepository.findByLoanAccountId(loanAccountId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private PaymentResponse mapToDto(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .paymentReference(p.getPaymentReference())
                .amountPaid(p.getAmountPaid())
                .paymentMode(p.getPaymentMode().name())
                .paymentStatus(p.getPaymentStatus().name())
                .paidAt(p.getPaidAt())

                .loanAccountNumber(p.getLoanAccount().getLoanAccountNumber())
                .installmentNo(
                        p.getEmiSchedule() != null
                                ? p.getEmiSchedule().getInstallmentNo()
                                : null
                )
                .build();
    }
}
