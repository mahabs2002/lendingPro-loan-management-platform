package com.loanapp.eligibility;

import com.loanapp.application.LoanApplicationRepository;
import com.loanapp.customer.CustomerRepository;
import com.loanapp.entity.CustomerProfile;
import com.loanapp.kyc.KycService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EligibilityService {

    private final CustomerRepository customerRepository;
    private final LoanApplicationRepository applicationRepository;
    private final KycService kycService;

    private static final BigDecimal MIN_INCOME = new BigDecimal("15000");
    private static final BigDecimal DEBT_RATIO = new BigDecimal("0.5");
    private static final int MIN_CREDIT_SCORE = 650;

    public EligibilityResult check(Long userId, BigDecimal requestedAmount) {
        // Rule 1: Profile must exist
        CustomerProfile profile = customerRepository.findByUserId(userId)
                .orElse(null);
        if (profile == null) return EligibilityResult.rejected("Customer profile incomplete");

        // Rule 2: KYC must be verified
        if (!kycService.isKycComplete(userId))
            return EligibilityResult.rejected("KYC verification not completed");

        // Rule 3: Minimum income
        if (profile.getMonthlyIncome().compareTo(MIN_INCOME) < 0)
            return EligibilityResult.rejected("Monthly income below minimum threshold of Rs.15,000");

        // Rule 4: Debt-to-income ratio
        BigDecimal existingEmi = applicationRepository.sumActiveEmiByUser(userId);
        BigDecimal maxEmiAllowed = profile.getMonthlyIncome().multiply(DEBT_RATIO);
        if (existingEmi.compareTo(maxEmiAllowed) >= 0)
            return EligibilityResult.rejected("Existing EMI obligations exceed 50% of income");

        // Rule 5: Mock credit score
        int creditScore = calculateMockCreditScore(profile);
        if (creditScore < MIN_CREDIT_SCORE)
            return EligibilityResult.rejected("Credit score too low: " + creditScore);

        // Rule 6: Requested amount not too high
        BigDecimal maxLoanable = profile.getMonthlyIncome().multiply(new BigDecimal("60"));
        if (requestedAmount.compareTo(maxLoanable) > 0)
            return EligibilityResult.rejected("Requested amount exceeds 60x monthly income limit");

        int score = computeFinalScore(profile, creditScore);
        return EligibilityResult.approved(score);
    }

    private int calculateMockCreditScore(CustomerProfile profile) {
        int base = 650;
        if (profile.getMonthlyIncome().compareTo(new BigDecimal("50000")) >= 0) base += 100;
        else if (profile.getMonthlyIncome().compareTo(new BigDecimal("25000")) >= 0) base += 50;
        return base + (profile.getId().intValue() % 50);
    }

    private int computeFinalScore(CustomerProfile profile, int creditScore) {
        int score = 0;
        if (profile.getMonthlyIncome().compareTo(new BigDecimal("50000")) >= 0) score += 30;
        else if (profile.getMonthlyIncome().compareTo(new BigDecimal("25000")) >= 0) score += 20;
        else score += 10;
        score += Math.min((creditScore - 600) / 5, 40);
        return Math.min(score, 100);
    }
}
