package com.loanapp.eligibility;

import com.loanapp.application.LoanApplicationRepository;
import com.loanapp.customer.CustomerRepository;
import com.loanapp.entity.CustomerProfile;
import com.loanapp.enums.EmploymentType;
import com.loanapp.kyc.KycService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EligibilityServiceTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private LoanApplicationRepository applicationRepository;
    @Mock private KycService kycService;
    @InjectMocks private EligibilityService eligibilityService;

    private CustomerProfile profile(BigDecimal income) {
        CustomerProfile p = new CustomerProfile();
        p.setId(1L); p.setMonthlyIncome(income);
        p.setEmploymentType(EmploymentType.SALARIED);
        return p;
    }

    @Test
    void shouldBeEligible_WhenAllRulesPassed() {
        when(customerRepository.findByUserId(1L)).thenReturn(Optional.of(profile(new BigDecimal("50000"))));
        when(kycService.isKycComplete(1L)).thenReturn(true);
        when(applicationRepository.sumActiveEmiByUser(1L)).thenReturn(BigDecimal.ZERO);
        EligibilityResult r = eligibilityService.check(1L, new BigDecimal("200000"));
        assertThat(r.isEligible()).isTrue();
    }

    @Test
    void shouldReject_WhenKycIncomplete() {
        when(customerRepository.findByUserId(1L)).thenReturn(Optional.of(profile(new BigDecimal("50000"))));
        when(kycService.isKycComplete(1L)).thenReturn(false);
        EligibilityResult r = eligibilityService.check(1L, new BigDecimal("100000"));
        assertThat(r.isEligible()).isFalse();
        assertThat(r.getReason()).contains("KYC");
    }

    @Test
    void shouldReject_WhenIncomeTooLow() {
        when(customerRepository.findByUserId(1L)).thenReturn(Optional.of(profile(new BigDecimal("10000"))));
        when(kycService.isKycComplete(1L)).thenReturn(true);
        EligibilityResult r = eligibilityService.check(1L, new BigDecimal("100000"));
        assertThat(r.isEligible()).isFalse();
    }
}
