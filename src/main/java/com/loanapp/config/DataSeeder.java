package com.loanapp.config;

import com.loanapp.entity.LoanProduct;
import com.loanapp.entity.User;
import com.loanapp.enums.Role;
import com.loanapp.product.LoanProductRepository;
import com.loanapp.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final LoanProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdminUser();
        seedLoanProducts();
    }

    private void seedAdminUser() {
        if (!userRepository.existsByEmail("admin@loanapp.com")) {
            User admin = User.builder()
                    .fullName("System Admin")
                    .email("admin@loanapp.com")
                    .mobile("9000000000")
                    .password(passwordEncoder.encode("Admin@1234"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Default admin created: admin@loanapp.com / Admin@1234");
        }
    }

    private void seedLoanProducts() {
        if (productRepository.count() == 0) {
            productRepository.save(LoanProduct.builder()
                    .productName("Personal Loan")
                    .minAmount(new BigDecimal("10000"))
                    .maxAmount(new BigDecimal("500000"))
                    .interestRate(new BigDecimal("14.5"))
                    .tenureMonths(60)
                    .processingFee(new BigDecimal("2.0"))
                    .activeStatus(true)
                    .build());

            productRepository.save(LoanProduct.builder()
                    .productName("Vehicle Loan")
                    .minAmount(new BigDecimal("50000"))
                    .maxAmount(new BigDecimal("2000000"))
                    .interestRate(new BigDecimal("10.5"))
                    .tenureMonths(84)
                    .processingFee(new BigDecimal("1.5"))
                    .activeStatus(true)
                    .build());

            productRepository.save(LoanProduct.builder()
                    .productName("Education Loan")
                    .minAmount(new BigDecimal("100000"))
                    .maxAmount(new BigDecimal("5000000"))
                    .interestRate(new BigDecimal("9.0"))
                    .tenureMonths(120)
                    .processingFee(new BigDecimal("1.0"))
                    .activeStatus(true)
                    .build());

            log.info("3 loan products seeded: Personal, Vehicle, Education");
        }
    }
}
