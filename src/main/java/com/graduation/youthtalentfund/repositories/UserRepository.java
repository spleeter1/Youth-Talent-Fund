package com.graduation.youthtalentfund.repositories;

import com.graduation.youthtalentfund.entities.User;
import com.graduation.youthtalentfund.repositories.Projection.StaffProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByCode(String code);
    Boolean existsByEmail(String email);
    Optional<User> findByEmailOrCode(String email, String code);

    @Query("SELECT u FROM User u JOIN FETCH u.userRoles ur JOIN FETCH ur.role WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    Optional<User> findByResetPasswordToken(String token);

    @Query(value = """
            SELECT
            	u.full_name AS fullName,
            	u.email,
            	u.phone_number AS phoneNumber,
            	u.address,
            	u.avatar_path AS avatarPath,
            	u.bio,
            	u.code,
            	u.status,
            	u.created_at AS createdAt,
            	COALESCE(SUM(CASE WHEN c.status = 'IN_PROGRESS' THEN 1 ELSE 0 END), 0) AS totalInProgress,
            	COALESCE(SUM(CASE WHEN c.status = 'COMPLETED' THEN 1 ELSE 0 END), 0) AS totalCompleted,
            	COALESCE(COUNT(d.id), 0) AS totalDonations
            FROM
            	users u
            JOIN user_roles ur ON
            	u.id = ur.user_id
            JOIN roles r ON
            	ur.role_id = r.id
            LEFT JOIN campaigns c ON
            	u.id = c.staff_id
            LEFT JOIN donations d ON 
                c.id = d.campaign_id
            WHERE
            	r.name = 'STAFF'
            	AND (:keyword IS NULL
            		OR u.full_name LIKE CONCAT('%', :keyword, '%')
            		OR u.email LIKE CONCAT('%', :keyword, '%')
            		OR u.code LIKE CONCAT('%', :keyword, '%'))
            GROUP BY
            	u.id
            """, nativeQuery = true)
    Page<StaffProjection> searchStaff(@Param("keyword") String keyword, Pageable pageable);
}
