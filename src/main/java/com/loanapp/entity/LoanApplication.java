package com.loanapp.entity;

import com.loanapp.enums.LoanApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_applications")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LoanApplication {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private LoanProduct product;

    @Column(name = "requested_amount", precision = 15, scale = 2)
    private BigDecimal requestedAmount;

    @Column(name = "requested_tenure")
    private Integer requestedTenure;

    private String purpose;

    @Column(name = "eligibility_score")
    private Integer eligibilityScore;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LoanApplicationStatus status = LoanApplicationStatus.PENDING;

    @Column(name = "admin_remarks", length = 1000)
    private String adminRemarks;

    @CreationTimestamp
    @Column(name = "applied_at", updatable = false)
    private LocalDateTime appliedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
