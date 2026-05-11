package com.loanapp.penalty;

import com.loanapp.emi.EmiRepository;
import com.loanapp.entity.*;
import com.loanapp.enums.PaidStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PenaltyService {

    private final PenaltyRepository penaltyRepository;
    private final EmiRepository emiRepository;

    private static final BigDecimal DAILY_PENALTY_RATE = new BigDecimal("0.002"); // 0.2% per day

    public void applyPenalty(EmiSchedule emi) {
        if (emi.getPaidStatus() != PaidStatus.OVERDUE) return;

        long daysOverdue = ChronoUnit.DAYS.between(emi.getDueDate(), LocalDate.now());
        if (daysOverdue <= 0) return;

        BigDecimal penaltyAmount = emi.getEmiAmount()
                .multiply(DAILY_PENALTY_RATE)
                .multiply(BigDecimal.valueOf(daysOverdue))
                .setScale(2, RoundingMode.HALF_UP);

        Penalty existing = penaltyRepository.findByEmiScheduleId(emi.getId()).orElse(null);

        if (existing != null) {
            existing.setDaysOverdue((int) daysOverdue);
            existing.setPenaltyAmount(penaltyAmount);
            penaltyRepository.save(existing);
        } else {
            Penalty penalty = Penalty.builder()
                    .emiSchedule(emi)
                    .daysOverdue((int) daysOverdue)
                    .penaltyAmount(penaltyAmount)
                    .status("ACTIVE")
                    .build();
            penaltyRepository.save(penalty);
        }

        // Update EMI penalty amount
        emi.setPenaltyAmount(penaltyAmount);
        emiRepository.save(emi);

        log.info("Penalty applied: EMI {} - {} days overdue - Rs.{}", emi.getId(), daysOverdue, penaltyAmount);
    }

    public void recalculatePendingPenalties() {
        List<Penalty> active = penaltyRepository.findByStatus("ACTIVE");
        active.forEach(p -> applyPenalty(p.getEmiSchedule()));
        log.info("Recalculated {} penalties", active.size());
    }

    public List<Penalty> getOverdueReport() {
        return penaltyRepository.findByStatus("ACTIVE");
    }
}
