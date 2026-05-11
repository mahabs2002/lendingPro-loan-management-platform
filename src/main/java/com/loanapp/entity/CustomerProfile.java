package com.loanapp.entity;

import com.loanapp.enums.EmploymentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_profile")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerProfile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private LocalDate dob;
    private String gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type")
    private EmploymentType employmentType;

    @Column(name = "employer_name")
    private String employerName;

    @Column(name = "monthly_income", precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "address_line1")
    private String addressLine1;

    private String city;
    private String state;
    private String pincode;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
