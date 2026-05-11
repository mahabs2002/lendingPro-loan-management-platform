package com.loanapp.customer;

import com.loanapp.entity.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerProfile, Long> {
    Optional<CustomerProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
