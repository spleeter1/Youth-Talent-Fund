package com.graduation.youthtalentfund.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "attachments", indexes = {
        @Index(name = "idx_attachment_filepath", columnList = "filePath", unique = true)
})
public class Attachment extends BaseEntity {

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String filePath;

    @Column(length = 100)
    private String fileType;

    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proof_report_id", nullable = false)
    private ProofReport proofReport;
}