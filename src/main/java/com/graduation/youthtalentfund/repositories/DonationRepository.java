package com.graduation.youthtalentfund.repositories;

import com.graduation.youthtalentfund.entities.Donation;
import com.graduation.youthtalentfund.repositories.Projection.DonationDataPublicProjection;
import com.graduation.youthtalentfund.repositories.Projection.TopDonatorProjection;
import com.graduation.youthtalentfund.repositories.Projection.TotalDonationStatisticProjection;
import com.graduation.youthtalentfund.repositories.Projection.UserDonationStatisticProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long>, JpaSpecificationExecutor<Donation> {
    Page<Donation> findByCampaignId(Long campaignId, Pageable pageable);
    Page<Donation> findByUserId(Long userId, Pageable pageable);

    Optional<Donation> findByCode(String code);
    Optional<Donation> findByTransactionCode(String code);

    @Query(
            value = """
                    SELECT
                        u.code AS userCode,
                        u.phoneNumber AS phoneNumber,
                        u.fullName AS fullName,
                        COUNT(d.id) AS donationCount,
                        COUNT(DISTINCT c.id) AS campaignCount,
                        COALESCE(SUM(d.amount), 0) AS totalDonated
                    FROM User u
                    LEFT JOIN Donation d
                        ON d.user = u
                        AND d.paymentStatus = 'PAID'
                        AND (:fromDate IS NULL OR d.createdAt >= :fromDate)
                        AND (:toDate IS NULL OR d.createdAt <= :toDate)
                    LEFT JOIN d.campaign c
                        ON (:campaignCode IS NULL OR c.code = :campaignCode)
                    WHERE (:userCode IS NULL OR u.code = :userCode)
                    GROUP BY u.id, u.code, u.phoneNumber, u.fullName
                    """,
            countQuery = """
                    SELECT COUNT(u.id)
                    FROM User u
                    WHERE (:userCode IS NULL OR u.code = :userCode)
                    """
    )
    Page<UserDonationStatisticProjection> getUserDonationStatistic(
            @Param("userCode") String userCode,
            @Param("campaignCode") String campaignCode,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    @Query("""
                SELECT
                    COALESCE(SUM(d.amount), 0) AS totalReceived,
                    COUNT(d.id) AS totalDonation,
                    SUM(CASE WHEN d.user IS NULL THEN 1 ELSE 0 END) AS guestDonation
                FROM Donation d
                WHERE d.paymentStatus = 'PAID'
                  AND (:fromDate IS NULL OR d.createdAt >= :fromDate)
                  AND (:toDate IS NULL OR d.createdAt <= :toDate)
            """)
    TotalDonationStatisticProjection getTotalDonationStatistic(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("""
                SELECT
                    u.fullName AS fullName,
                    u.phoneNumber AS phoneNumber,
                    SUM(d.amount) AS amount
                FROM Donation d
                JOIN d.user u
                WHERE d.paymentStatus = 'PAID'
                  AND (:fromDate IS NULL OR d.createdAt >= :fromDate)
                  AND (:toDate IS NULL OR d.createdAt <= :toDate)
                GROUP BY u.id, u.fullName, u.phoneNumber
                ORDER BY SUM(d.amount) DESC
            """)
    Page<TopDonatorProjection> findTopDonators(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    @Query("""
        SELECT
            CASE
                WHEN d.isAnonymous = true THEN NULL
                ELSE d.donorName
            END AS donorName,
            d.amount AS amount,
            d.createdAt AS time
        FROM Donation d
        WHERE d.campaign.code = :campaignCode
          AND d.paymentStatus = 'PAID'
        ORDER BY d.updatedAt DESC
        """)
    Page<DonationDataPublicProjection> findDonationPublicListByCampaignCode(
            @Param("campaignCode") String campaignCode,
            Pageable pageable
    );

    @Query("""
                SELECT
                    CASE
                        WHEN d.isAnonymous = true THEN NULL
                        ELSE d.donorName
                    END AS donorName,
                    d.amount AS amount,
                    d.createdAt AS time
                FROM Donation d
                WHERE d.paymentStatus = 'PAID'
                ORDER BY d.updatedAt DESC
            """)
    Page<DonationDataPublicProjection> getRecentPublicDonationList(Pageable pageable);

}
