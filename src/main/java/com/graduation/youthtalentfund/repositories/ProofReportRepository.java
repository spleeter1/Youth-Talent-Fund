package com.graduation.youthtalentfund.repositories;

import com.graduation.youthtalentfund.entities.ProofReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProofReportRepository extends JpaRepository<ProofReport, Long> {
    Page<ProofReport> findByCampaignId(Long campaignId, Pageable pageable);
}