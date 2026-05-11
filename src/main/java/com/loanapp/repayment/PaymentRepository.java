package com.loanapp.repayment;

import com.loanapp.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByLoanAccountId(Long loanAccountId);

    @Query("SELECT COALESCE(SUM(p.amountPaid), 0) FROM Payment p WHERE p.loanAccount.id = :accountId AND p.paymentStatus = 'SUCCESS'")
    BigDecimal sumPaidByAccount(Long accountId);

    @Query("SELECT p FROM Payment p WHERE p.paidAt BETWEEN :start AND :end")
    List<Payment> findByDateRange(LocalDateTime start, LocalDateTime end);
}
