package com.graduation.youthtalentfund.repositories;

import com.graduation.youthtalentfund.entities.Campaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    Optional<Campaign> findBySlug(String slug);
    Optional<Campaign> findByCode(String code);

    @Query(value = "SELECT c FROM Campaign c LEFT JOIN FETCH c.staff",
            countQuery = "SELECT count(c) FROM Campaign c")
    Page<Campaign> findAllWithStaff(Pageable pageable);

    @Query("SELECT c FROM Campaign c " +
            "LEFT JOIN FETCH c.staff " +
            "WHERE c.slug = :slug")
    Optional<Campaign> findBySlugWithStaff(String slug);
}