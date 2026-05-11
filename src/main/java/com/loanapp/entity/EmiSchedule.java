package com.loanapp.entity;

import com.loanapp.enums.PaidStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "emi_schedule")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EmiSchedule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_account_id", nullable = false)
    private LoanAccount loanAccount;

    @Column(name = "installment_no")
    private Integer installmentNo;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "emi_amount", precision = 15, scale = 2)
    private BigDecimal emiAmount;

    @Column(name = "principal_amount", precision = 15, scale = 2)
    private BigDecimal principalAmount;

    @Column(name = "interest_amount", precision = 15, scale = 2)
    private BigDecimal interestAmount;

    @Column(name = "penalty_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal penaltyAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "paid_status")
    @Builder.Default
    private PaidStatus paidStatus = PaidStatus.PENDING;

    @Column(name = "paid_date")
    private LocalDate paidDate;
}
