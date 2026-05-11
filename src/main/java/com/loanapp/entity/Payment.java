package com.loanapp.entity;

import com.loanapp.enums.PaymentMode;
import com.loanapp.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_account_id", nullable = false)
    private LoanAccount loanAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emi_id")
    private EmiSchedule emiSchedule;

    @Column(name = "payment_reference", unique = true)
    private String paymentReference;

    @Column(name = "amount_paid", precision = 15, scale = 2)
    private BigDecimal amountPaid;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode")
    private PaymentMode paymentMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.SUCCESS;

    @CreationTimestamp
    @Column(name = "paid_at", updatable = false)
    private LocalDateTime paidAt;
}
