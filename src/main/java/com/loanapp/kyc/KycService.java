package com.loanapp.kyc;

import com.loanapp.audit.AuditLogService;
import com.loanapp.auth.UserRepository;
import com.loanapp.entity.*;
import com.loanapp.enums.KycStatus;
import com.loanapp.enums.NotificationType;
import com.loanapp.exception.*;
import com.loanapp.kyc.dto.*;
import com.loanapp.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KycService {

    private final KycRepository kycRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    // Allowed file types
    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf", "image/jpeg", "image/png", "image/jpg"
    );

    // Max file size: 5MB
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    public KycDocument uploadDocument(Long userId, KycUploadDto dto, MultipartFile file) throws IOException {

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BadRequestException("Only PDF, JPG, PNG files are allowed");
        }

        // Validate file size
        if (file.getSize() > MAX_SIZE) {
            throw new BadRequestException("File size must be under 5MB");
        }

        // Validate file not empty
        if (file.isEmpty()) {
            throw new BadRequestException("File cannot be empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // If same document type already exists — update it (re-upload)
        KycDocument doc = kycRepository
                .findByUserIdAndDocumentType(userId, dto.getDocumentType())
                .orElse(new KycDocument());

        doc.setUser(user);
        doc.setDocumentType(dto.getDocumentType());
        doc.setDocumentNumber(dto.getDocumentNumber());

        // Store actual file bytes in DB
        doc.setFileData(file.getBytes());
        doc.setFileName(file.getOriginalFilename());
        doc.setFileType(contentType);
        doc.setFileSize(file.getSize());

        // Reset to pending on re-upload
        doc.setStatus(KycStatus.PENDING);
        doc.setRemarks(null);
        doc.setVerifiedBy(null);

        return kycRepository.save(doc);
    }

    public List<KycDocument> getMyDocuments(Long userId) {
        return kycRepository.findByUserId(userId);
    }

    public List<KycResponse> getPendingKyc() {
        return kycRepository.findByStatus(KycStatus.PENDING)
                .stream()
                .map(doc->KycResponse.builder()
                .id(doc.getId())
                .documentType(doc.getDocumentType().name())
                .documentNumber(doc.getDocumentNumber())
                        .fileName(doc.getFileName())
                        .fileType(doc.getFileType())
                        .fileSize(doc.getFileSize())
                .status(doc.getStatus().name())
                        .remarks(doc.getRemarks())
                .createdAt(doc.getCreatedAt())
                .verifiedBy(doc.getVerifiedBy())
                        .build()).toList();

    }

    public KycDocument verifyDocument(Long docId, Long adminId, KycVerifyDto dto) {
        KycDocument doc = kycRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("KYC document not found"));

        String oldStatus = doc.getStatus().name();
        doc.setStatus(dto.getStatus());
        doc.setRemarks(dto.getRemarks());
        doc.setVerifiedBy(adminId);
        kycRepository.save(doc);

        auditLogService.log(adminId, "KYC_VERIFY", "KycDocument",
                docId, oldStatus, dto.getStatus().name(), null);

        NotificationType type = dto.getStatus() == KycStatus.VERIFIED
                ? NotificationType.KYC_VERIFIED : NotificationType.KYC_REJECTED;
        String msg = dto.getStatus() == KycStatus.VERIFIED
                ? "Your KYC document has been verified."
                : "Your KYC document was rejected. Reason: " + dto.getRemarks();
        notificationService.send(doc.getUser().getId(), type, msg);

        return doc;
    }

    // Download file bytes — used by download endpoint
    public KycDocument getDocumentById(Long docId) {
        return kycRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + docId));
    }

    public boolean isKycComplete(Long userId) {
        return kycRepository.countByUserIdAndStatus(userId, KycStatus.VERIFIED) >= 1;
    }
}
