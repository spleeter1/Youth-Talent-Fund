package com.graduation.youthtalentfund.repositories;

import com.graduation.youthtalentfund.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByUserIdAndRoleId(Long userId, Integer roleId);
}
