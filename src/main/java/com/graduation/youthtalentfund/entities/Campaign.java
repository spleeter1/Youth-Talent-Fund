package com.graduation.youthtalentfund.entities;

import com.graduation.youthtalentfund.enums.CampaignCategory;
import com.graduation.youthtalentfund.enums.CampaignStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "campaigns", indexes = {
        @Index(name = "idx_campaign_code", columnList = "code", unique = true),
        @Index(name = "idx_campaign_slug", columnList = "slug", unique = true)
})
public class Campaign extends BaseEntity{
    @Column(length=50,nullable = false)
    private String code;

    @Column(nullable = false)
    private String slug;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String description;

    @Column
    private String location;

    @Lob
    private String story;

    @Column
    private String coverImagePath;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal targetAmount;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private CampaignCategory category;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private CampaignStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private User staff;

    // Quan hệ Một (Campaign) - Nhiều (Donation)
    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Donation> donations = new HashSet<>();

    // Quan hệ Một (Campaign) - Nhiều (ProofReport)
    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProofReport> proofReports = new HashSet<>();
}
