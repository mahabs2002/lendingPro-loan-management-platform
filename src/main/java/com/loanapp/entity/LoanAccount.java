package com.loanapp.entity;

import com.loanapp.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_accounts")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LoanAccount {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private LoanApplication application;

    @Column(name = "loan_account_number", unique = true, nullable = false)
    private String loanAccountNumber;

    @Column(name = "approved_amount", precision = 15, scale = 2)
    private BigDecimal approvedAmount;

    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "tenure_months")
    private Integer tenureMonths;

    @Column(name = "disbursed_date")
    private LocalDate disbursedDate;

    @Column(name = "outstanding_balance", precision = 15, scale = 2)
    private BigDecimal outstandingBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_status")
    @Builder.Default
    private LoanStatus loanStatus = LoanStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
