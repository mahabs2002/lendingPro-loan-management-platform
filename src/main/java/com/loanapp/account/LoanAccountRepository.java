package com.loanapp.account;

import com.loanapp.entity.LoanAccount;
import com.loanapp.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LoanAccountRepository extends JpaRepository<LoanAccount, Long> {
    Optional<LoanAccount> findByApplicationId(Long applicationId);
    Optional<LoanAccount> findByLoanAccountNumber(String accountNumber);
    List<LoanAccount> findByLoanStatus(LoanStatus status);
    List<LoanAccount> findByApplicationUserId(Long userId);
}
