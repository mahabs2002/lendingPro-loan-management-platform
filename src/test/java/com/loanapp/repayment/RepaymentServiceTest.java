package com.loanapp.repayment;

import com.loanapp.account.LoanAccountRepository;
import com.loanapp.audit.AuditLogService;
import com.loanapp.emi.EmiRepository;
import com.loanapp.entity.*;
import com.loanapp.enums.*;
import com.loanapp.notification.NotificationService;
import com.loanapp.repayment.dto.PaymentRequest;
import com.loanapp.repayment.dto.PaymentResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private LoanAccountRepository loanAccountRepository;
    @Mock private EmiRepository emiRepository;
    @Mock private NotificationService notificationService;
    @Mock private AuditLogService auditLogService;
    @InjectMocks private RepaymentService repaymentService;

    @Test
    void makePayment_ShouldMarkEmiPaid_WhenFullAmountPaid() {
        LoanProduct product = new LoanProduct();
        product.setInterestRate(new BigDecimal("10"));
        User user = User.builder().id(1L).fullName("Test").email("t@t.com").build();
        LoanApplication app = new LoanApplication();
        app.setUser(user); app.setProduct(product);

        LoanAccount account = new LoanAccount();
        account.setId(1L); account.setLoanAccountNumber("LN001");
        account.setApprovedAmount(new BigDecimal("100000"));
        account.setOutstandingBalance(new BigDecimal("90000"));
        account.setLoanStatus(LoanStatus.ACTIVE); account.setApplication(app);

        EmiSchedule emi = EmiSchedule.builder()
                .id(1L).loanAccount(account)
                .emiAmount(new BigDecimal("9000")).penaltyAmount(BigDecimal.ZERO)
                .paidStatus(PaidStatus.PENDING).dueDate(LocalDate.now().plusDays(5)).build();

        when(loanAccountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(emiRepository.findFirstByLoanAccountIdAndPaidStatusOrderByInstallmentNo(1L, PaidStatus.PENDING))
                .thenReturn(Optional.of(emi));
        when(paymentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(emiRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(loanAccountRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        doNothing().when(notificationService).send(any(), any(), any());
        doNothing().when(auditLogService).log(any(), any(), any(), any(), any(), any(), any());

        PaymentRequest req = new PaymentRequest();
        req.setLoanAccountId(1L); req.setAmountPaid(new BigDecimal("9000"));
        req.setPaymentMode(PaymentMode.UPI);

        PaymentResponse payment = repaymentService.makePayment(1L, req);
        assertThat(payment).isNotNull();
        assertThat(emi.getPaidStatus()).isEqualTo(PaidStatus.PAID);
        assertThat(account.getOutstandingBalance()).isEqualByComparingTo(new BigDecimal("81000"));
    }
}
