package com.graduation.youthtalentfund.repositories;

import com.graduation.youthtalentfund.dtos.response.CampaignCountResponse;
import com.graduation.youthtalentfund.entities.Campaign;
import com.graduation.youthtalentfund.enums.CampaignStatus;
import com.graduation.youthtalentfund.repositories.Projection.CampaignCountProjection;
import com.graduation.youthtalentfund.repositories.Projection.CampaignDetailProjection;
import com.graduation.youthtalentfund.repositories.Projection.CampaignShortProjection;
import com.graduation.youthtalentfund.repositories.Projection.CampaignStatisticProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    Optional<Campaign> findBySlug(String slug);

    Optional<Campaign> findByCode(String code);

    boolean existsBySlug(String slug);

    @Query(value = "SELECT c FROM Campaign c LEFT JOIN FETCH c.staff",
            countQuery = "SELECT count(c) FROM Campaign c")
    Page<Campaign> findAllWithStaff(Pageable pageable);

    @Query("SELECT c FROM Campaign c " +
            "LEFT JOIN FETCH c.staff " +
            "WHERE c.slug = :slug")
    Optional<Campaign> findBySlugWithStaff(String slug);

    @Query(value = """
            SELECT
                c.category AS category,
                DATEDIFF(c.end_date, NOW()) AS durationsDays,
                c.title AS title,
                c.description AS description,
                c.current_amount AS currentAmount,
                c.target_amount AS targetAmount,
                c.start_date AS startDate,
                c.end_date AS endDate,
                c.cover_image_path AS coverImagePath,
                c.code AS code
            FROM campaigns c
            WHERE (:status IS NULL OR c.status = :status)
              AND (:category IS NULL OR c.category = :category)
              AND (
                    :keyword IS NULL
                    OR c.title LIKE CONCAT('%', :keyword, '%')
                    OR c.description LIKE CONCAT('%', :keyword, '%')
              )
            ORDER BY DATEDIFF(c.end_date, NOW()) ASC
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM campaigns c
                    WHERE (:status IS NULL OR c.status = :status)
                      AND (:category IS NULL OR c.category = :category)
                      AND (
                             :keyword IS NULL
                             OR c.title LIKE CONCAT('%', :keyword, '%')
                             OR c.description LIKE CONCAT('%', :keyword, '%')
                      )
                    """,
            nativeQuery = true)
    Page<CampaignShortProjection> findAllCampaignsShort(
            @Param("status") String status,
            @Param("category") String category,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query(value = """
            SELECT
                c.category AS category,
                DATEDIFF(c.end_date, NOW()) AS durationsDays,
                c.title AS title,
                c.description AS description,
                c.current_amount AS currentAmount,
                c.target_amount AS targetAmount,
                c.start_date AS startDate,
                c.end_date AS endDate,
                c.cover_image_path AS coverImagePath,
                c.code AS code
            FROM campaigns c
            WHERE c.staff_id = :managerId
              AND (:status IS NULL OR c.status = :status)
              AND (:category IS NULL OR c.category = :category)
              AND (
                    :keyword IS NULL
                    OR c.title LIKE CONCAT('%', :keyword, '%')
                    OR c.description LIKE CONCAT('%', :keyword, '%')
              )
            ORDER BY DATEDIFF(c.end_date, NOW()) ASC
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM campaigns c
            WHERE c.staff_id = :managerId
              AND (:status IS NULL OR c.status = :status)
              AND (:category IS NULL OR c.category = :category)
              AND (
                    :keyword IS NULL
                    OR c.title LIKE CONCAT('%', :keyword, '%')
                    OR c.description LIKE CONCAT('%', :keyword, '%')
              )
            """,
            nativeQuery = true)
    Page<CampaignShortProjection> findManagedCampaignsShort(
            @Param("managerId") Long managerId,
            @Param("status") String status,
            @Param("category") String category,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // Get Campaign detail
    @Query(value = """
            SELECT
            	c.code AS code,
            	c.slug AS slug,
            	c.title AS title,
            	c.cover_image_path AS coverImagePath,
            	c.description AS description,
            	c.story AS story,
            	c.location AS location,
            	c.start_date AS startDate,
            	c.end_date AS endDate,
            	c.current_amount AS currentAmount,
            	c.target_amount AS targetAmount,
            	c.status AS status,
            	DATEDIFF(c.end_date, c.start_date) AS durationDays,
            	s.id AS staffId,
            	s.code AS staffCode,
            	s.full_name AS staffName,
            	s.email AS staffEmail
            FROM
            	campaigns c
            LEFT JOIN users s ON
            	c.staff_id = s.id
            WHERE
            	c.code = :value
            	OR c.slug = :value
        """,
            nativeQuery = true)
    Optional<CampaignDetailProjection> findByCodeOrSlug(@Param("value") String value);

    @Query("SELECT c FROM Campaign c WHERE c.status NOT IN (:excludedStatuses)")
    List<Campaign> findAllActiveCampaigns(@Param("excludedStatuses") List<CampaignStatus> excludedStatuses);

    @Query(
            value = """
            SELECT
                c.code AS campaignCode,
                c.title AS title,
                u.code AS staffCode,
                COUNT(d.id) AS donationCount,
                COALESCE(SUM(d.amount), 0) AS totalReceived
            FROM campaigns c
            JOIN users u ON u.id = c.staff_id
            LEFT JOIN donations d ON d.campaign_id = c.id
                AND (:fromDate IS NULL OR d.created_at >= :fromDate)
                AND (:toDate IS NULL OR d.created_at <= :toDate)
            WHERE (:campaignCode IS NULL OR c.code = :campaignCode)
            GROUP BY c.code, c.title, u.code
        """,
            countQuery = """
            SELECT COUNT(*)
            FROM campaigns c
            WHERE (:campaignCode IS NULL OR c.code = :campaignCode)
        """,
            nativeQuery = true
    )
    Page<CampaignStatisticProjection> getCampaignStatistic(
            @Param("campaignCode") String campaignCode,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    @Query("""
                SELECT
                    COALESCE(SUM(CASE 
                        WHEN c.status = 'IN_PROGRESS' THEN 1 
                        ELSE 0 
                    END), 0) AS activeCampaign,
            
                    COALESCE(SUM(CASE 
                        WHEN c.status = 'COMPLETED' THEN 1 
                        ELSE 0 
                    END), 0) AS finishedCampaign,
            
                    COUNT(c) AS totalCampaign
                FROM Campaign c
                WHERE (:fromDate IS NULL OR c.startDate >= :fromDate)
                  AND (:toDate IS NULL OR c.startDate <= :toDate)
            """)
    CampaignCountProjection getCampaignCount(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

}