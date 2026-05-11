package com.loanapp.penalty;

import com.loanapp.entity.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {
    Optional<Penalty> findByEmiScheduleId(Long emiId);
    List<Penalty> findByStatus(String status);
}
