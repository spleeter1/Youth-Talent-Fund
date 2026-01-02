package com.graduation.youthtalentfund.repositories;

import com.graduation.youthtalentfund.entities.Donation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    Page<Donation> findByCampaignId(Long campaignId, Pageable pageable);
    Page<Donation> findByUserId(Long userId, Pageable pageable);
}
