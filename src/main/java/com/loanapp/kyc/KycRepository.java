package com.loanapp.kyc;

import com.loanapp.entity.KycDocument;
import com.loanapp.enums.DocumentType;
import com.loanapp.enums.KycStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface KycRepository extends JpaRepository<KycDocument, Long> {
    List<KycDocument> findByUserId(Long userId);
    List<KycDocument> findByStatus(KycStatus status);
    Optional<KycDocument> findByUserIdAndDocumentType(Long userId, DocumentType type);
    long countByUserIdAndStatus(Long userId, KycStatus status);
}
