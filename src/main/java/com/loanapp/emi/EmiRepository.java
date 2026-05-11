package com.loanapp.emi;

import com.loanapp.entity.EmiSchedule;
import com.loanapp.enums.PaidStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmiRepository extends JpaRepository<EmiSchedule, Long> {
    List<EmiSchedule> findByLoanAccountIdOrderByInstallmentNo(Long loanAccountId);
    List<EmiSchedule> findByDueDateAndPaidStatus(LocalDate dueDate, PaidStatus status);
    List<EmiSchedule> findByDueDateBeforeAndPaidStatus(LocalDate date, PaidStatus status);
    Optional<EmiSchedule> findFirstByLoanAccountIdAndPaidStatusOrderByInstallmentNo(Long accountId, PaidStatus status);

    @Query("SELECT COUNT(e) FROM EmiSchedule e WHERE e.loanAccount.id = :accountId AND e.paidStatus = 'PENDING'")
    long countPendingByLoanAccountId(Long accountId);
}
