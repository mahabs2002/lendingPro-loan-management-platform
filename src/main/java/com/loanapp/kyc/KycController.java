package com.loanapp.kyc;

import com.loanapp.auth.UserRepository;
import com.loanapp.common.ApiResponse;
import com.loanapp.entity.KycDocument;
import com.loanapp.kyc.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;
    private final UserRepository userRepository;

    // ─── Upload KYC document (multipart/form-data) ──────────────────────────
    // Postman: Body → form-data
    //   Key: file         Type: File   → select PDF/JPG/PNG
    //   Key: documentType Type: Text   → AADHAAR / PAN / PASSPORT
    //   Key: documentNumber Type: Text → 1234-5678-9012
    @PostMapping(value = "/api/kyc/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> upload(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("documentType") String documentType,
            @RequestParam("documentNumber") String documentNumber,
            @RequestParam("file") MultipartFile file) throws Exception {

        Long userId = getUserId(userDetails);

        KycUploadDto dto = new KycUploadDto();
        dto.setDocumentType(com.loanapp.enums.DocumentType.valueOf(documentType));
        dto.setDocumentNumber(documentNumber);

        KycDocument saved = kycService.uploadDocument(userId, dto, file);

        return ResponseEntity.ok(ApiResponse.ok(
                "Document uploaded successfully",
                "ID: " + saved.getId() + " | File: " + saved.getFileName()
                        + " | Size: " + (saved.getFileSize() / 1024) + " KB"
        ));
    }

    // ─── Download KYC document by ID ────────────────────────────────────────
    @GetMapping("/api/kyc/download/{docId}")
    public ResponseEntity<byte[]> download(@PathVariable Long docId) {
        KycDocument doc = kycService.getDocumentById(docId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getFileName() + "\"")
                .body(doc.getFileData());
    }

    // ─── View inline in browser (PDF/Image preview) ─────────────────────────
    @GetMapping("/api/kyc/view/{docId}")
    public ResponseEntity<byte[]> viewInline(@PathVariable Long docId) {
        KycDocument doc = kycService.getDocumentById(docId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + doc.getFileName() + "\"")
                .body(doc.getFileData());
    }

    // ─── View my uploaded documents (metadata only, no file bytes) ──────────
    @GetMapping("/api/kyc/my-documents")
    public ResponseEntity<ApiResponse<List<KycDocument>>> myDocs(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(
                kycService.getMyDocuments(getUserId(userDetails))));
    }

    // ─── Admin: view all pending KYC ────────────────────────────────────────
    @GetMapping("/api/admin/kyc/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<KycResponse>>> pendingKyc() {
        return ResponseEntity.ok(ApiResponse.ok(kycService.getPendingKyc()));
    }

    // ─── Admin: verify or reject KYC ───────────────────────────────────────
    @PutMapping("/api/admin/kyc/{docId}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<KycDocument>> verify(
            @PathVariable Long docId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody KycVerifyDto dto) {
        Long adminId = getUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.ok("KYC updated",
                kycService.verifyDocument(docId, adminId, dto)));
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
    }
}
