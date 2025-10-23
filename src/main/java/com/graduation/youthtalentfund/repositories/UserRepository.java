package com.graduation.youthtalentfund.repositories;

import com.graduation.youthtalentfund.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByCode(String code);
    Boolean existsByEmail(String email);
}
