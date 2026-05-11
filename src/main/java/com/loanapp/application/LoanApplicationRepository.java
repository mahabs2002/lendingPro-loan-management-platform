package com.loanapp.application;

import com.loanapp.entity.LoanApplication;
import com.loanapp.enums.LoanApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findByUserId(Long userId);
    List<LoanApplication> findByStatus(LoanApplicationStatus status);

    @Query("SELECT COALESCE(SUM(la.approvedAmount / la.tenureMonths), 0) " +
           "FROM LoanAccount la WHERE la.application.user.id = :userId AND la.loanStatus = 'ACTIVE'")
    BigDecimal sumActiveEmiByUser(Long userId);
}
