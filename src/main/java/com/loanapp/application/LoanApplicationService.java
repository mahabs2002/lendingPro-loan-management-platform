package com.loanapp.application;


import com.loanapp.application.dto.LoanApplicationRequest;
import com.loanapp.application.dto.LoanApplicationResponse;
import com.loanapp.auth.UserRepository;
import com.loanapp.eligibility.EligibilityResult;
import com.loanapp.eligibility.EligibilityService;
import com.loanapp.entity.*;
import com.loanapp.enums.LoanApplicationStatus;
import com.loanapp.exception.*;
import com.loanapp.product.LoanProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanApplicationService {

    private final LoanApplicationRepository repository;
    private final UserRepository userRepository;
    private final LoanProductRepository productRepository;
    private final EligibilityService eligibilityService;

    public LoanApplication apply(Long userId, LoanApplicationRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        LoanProduct product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Loan product not found"));

        if (!product.getActiveStatus())
            throw new BadRequestException("Loan product is not active");
        if (req.getRequestedAmount().compareTo(product.getMinAmount()) < 0 ||
            req.getRequestedAmount().compareTo(product.getMaxAmount()) > 0)
            throw new BadRequestException("Requested amount out of product range");

        EligibilityResult result = eligibilityService.check(userId, req.getRequestedAmount());

        LoanApplication application = LoanApplication.builder()
                .user(user)
                .product(product)
                .requestedAmount(req.getRequestedAmount())
                .requestedTenure(req.getRequestedTenure())
                .purpose(req.getPurpose())
                .eligibilityScore(result.getScore())
                .status(result.isEligible() ? LoanApplicationStatus.PENDING : LoanApplicationStatus.REJECTED)
                .adminRemarks(result.isEligible() ? null : result.getReason())
                .build();

        return repository.save(application);
    }

   /* public List<LoanApplication> getMyApplications(Long userId) {
        return repository.findByUserId(userId);
    }*/


    public  List<LoanApplicationResponse> getMyApplications(Long userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(app->LoanApplicationResponse.builder()
                .id(app.getId())
                        .requestedAmount(app.getRequestedAmount())
                        .requestedTenure(app.getRequestedTenure())
                        .purpose(app.getPurpose())
                        .eligibilityScore(app.getEligibilityScore())
                        .status(app.getStatus().name())
                        .appliedAt(app.getAppliedAt())
                        .productName(app.getProduct().getProductName())
                        .build()).toList();
    }




    public LoanApplication getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + id));
    }

    public List<LoanApplicationResponse> getPendingApplications() {
        return repository.findByStatus(LoanApplicationStatus.PENDING)
                .stream()
                .map(app->LoanApplicationResponse.builder()
                .id(app.getId())
                        .requestedAmount(app.getRequestedAmount())
                        .requestedTenure(app.getRequestedTenure())
                        .purpose(app.getPurpose())
                        .eligibilityScore(app.getEligibilityScore())
                        .status(app.getStatus().name())
                        .appliedAt(app.getAppliedAt())
                        .productName(app.getProduct().getProductName())
                        .userEmail(app.getUser().getEmail())
                        .build()).toList();
    }

    public List<LoanApplicationResponse> getAllApplications() {
        return repository.findAll()
                .stream()
                .map(app->LoanApplicationResponse.builder()
                .id(app.getId())
                        .requestedAmount(app.getRequestedAmount())
                        .requestedTenure(app.getRequestedTenure())
                        .purpose(app.getPurpose())
                        .eligibilityScore(app.getEligibilityScore())
                        .status(app.getStatus().name())
                        .appliedAt(app.getAppliedAt())
                        .productName(app.getProduct().getProductName())
                        .userEmail(app.getUser().getEmail())
                        .adminRemarks(app.getAdminRemarks())
                        .build()).toList();
    }
}
