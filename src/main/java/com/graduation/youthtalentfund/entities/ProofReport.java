package com.graduation.youthtalentfund.entities;

import com.graduation.youthtalentfund.enums.ProofReportType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "proof_reports", indexes = {
        @Index(name = "idx_proofreport_code", columnList = "code", unique = true)
})
public class ProofReport extends BaseEntity {

    @Column(length = 50, nullable = false)
    private String code;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private ProofReportType type;

    @Column(precision = 15, scale = 2)
    private BigDecimal transactionAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @OneToMany(mappedBy = "proofReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments;
}