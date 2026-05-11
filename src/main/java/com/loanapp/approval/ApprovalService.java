package com.loanapp.approval;

import com.loanapp.account.LoanAccountService;
import com.loanapp.application.LoanApplicationRepository;
import com.loanapp.application.dto.LoanApplicationResponse;
import com.loanapp.approval.dto.ApprovalRequest;
import com.loanapp.audit.AuditLogService;
import com.loanapp.emi.EmiSchedulerService;
import com.loanapp.entity.*;
import com.loanapp.enums.LoanApplicationStatus;
import com.loanapp.exception.*;
import com.loanapp.notification.NotificationService;
import com.loanapp.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final LoanApplicationRepository applicationRepository;
    private final LoanAccountService loanAccountService;
    private final EmiSchedulerService emiSchedulerService;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    @Transactional
    public LoanApplicationResponse processDecision(Long applicationId, Long adminId, ApprovalRequest req) {
        LoanApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        if (application.getStatus() != LoanApplicationStatus.PENDING &&
            application.getStatus() != LoanApplicationStatus.UNDER_REVIEW) {
            throw new BadRequestException("Application is not in a reviewable state");
        }

        String oldStatus = application.getStatus().name();
        application.setStatus(req.getDecision());
        application.setAdminRemarks(req.getRemarks());
        applicationRepository.save(application);

        auditLogService.log(adminId, "LOAN_DECISION", "LoanApplication",
                applicationId, oldStatus, req.getDecision().name(), null);

        if (req.getDecision() == LoanApplicationStatus.APPROVED) {
            java.math.BigDecimal amount = req.getApprovedAmount() != null
                    ? req.getApprovedAmount() : application.getRequestedAmount();
            LoanAccount account = loanAccountService.createAccount(application, amount, req.getApprovedTenure());
            emiSchedulerService.generateSchedule(account);
        }

        if (req.getDecision() == LoanApplicationStatus.REJECTED) {
            notificationService.send(application.getUser().getId(),
                    NotificationType.LOAN_REJECTED,
                    "Your loan application was rejected. Reason: " + req.getRemarks());
        }

        if (req.getDecision() == LoanApplicationStatus.DOCS_REQUESTED) {
            notificationService.send(application.getUser().getId(),
                    NotificationType.LOAN_REJECTED,
                    "Additional documents required: " + req.getRemarks());
        }

        return mapToDto(application);
    }

    public LoanApplicationResponse markUnderReview(Long applicationId, Long adminId) {
        LoanApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        app.setStatus(LoanApplicationStatus.UNDER_REVIEW);
        applicationRepository.save(app);
        auditLogService.log(adminId, "MARK_UNDER_REVIEW", "LoanApplication", applicationId, "PENDING", "UNDER_REVIEW", null);
        return mapToDto(app);
    }

    private LoanApplicationResponse mapToDto(LoanApplication app) {
        return LoanApplicationResponse.builder()
                .id(app.getId())
                .requestedAmount(app.getRequestedAmount())
                .requestedTenure(app.getRequestedTenure())
                .purpose(app.getPurpose())
                .eligibilityScore(app.getEligibilityScore())
                .status(app.getStatus().name())
                .appliedAt(app.getAppliedAt())

                // safe now
                .productName(app.getProduct().getProductName())
                .userEmail(app.getUser().getEmail())

                .adminRemarks(app.getAdminRemarks())
                .build();
    }
}
