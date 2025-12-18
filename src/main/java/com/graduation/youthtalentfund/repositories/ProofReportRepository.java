package com.graduation.youthtalentfund.repositories;

import com.graduation.youthtalentfund.entities.ProofReport;
import com.graduation.youthtalentfund.enums.ProofReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProofReportRepository extends JpaRepository<ProofReport, Long> {
    Page<ProofReport> findByCampaignId(Long campaignId, Pageable pageable);

    @Query("""
            SELECT pr
            FROM ProofReport pr
            WHERE pr.campaign.code = :campaignCode
                AND (:type IS NULL OR pr.type = :type)
            ORDER BY pr.createdAt DESC
            """)
    Page<ProofReport> findPageByCampaign(
            @Param("campaignCode") String campaignCode,
            @Param("type") ProofReportType type,
            Pageable pageable
    );
}