package com.loanapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.loanapp.enums.DocumentType;
import com.loanapp.enums.KycStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_documents")

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class KycDocument {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type")
    private DocumentType documentType;

    @Column(name = "document_number")
    private String documentNumber;

    // Stores actual file bytes (PDF/JPG/PNG) in MySQL LONGBLOB
    @Lob
    @Column(name = "file_data", columnDefinition = "LONGBLOB")
    private byte[] fileData;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private KycStatus status = KycStatus.PENDING;

    @Column(name = "verified_by")
    private Long verifiedBy;

    private String remarks;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
