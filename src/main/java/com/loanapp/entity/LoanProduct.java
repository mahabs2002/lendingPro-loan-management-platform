package com.loanapp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_products")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LoanProduct {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "min_amount", precision = 15, scale = 2)
    private BigDecimal minAmount;

    @Column(name = "max_amount", precision = 15, scale = 2)
    private BigDecimal maxAmount;

    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "tenure_months")
    private Integer tenureMonths;

    @Column(name = "processing_fee", precision = 5, scale = 2)
    private BigDecimal processingFee;

    @Column(name = "active_status")
    @Builder.Default
    private Boolean activeStatus = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
