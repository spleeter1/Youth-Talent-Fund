package com.graduation.youthtalentfund.config;

import com.graduation.youthtalentfund.entities.Role;
import com.graduation.youthtalentfund.entities.User;
import com.graduation.youthtalentfund.entities.UserRole;
import com.graduation.youthtalentfund.enums.UserStatus;
import com.graduation.youthtalentfund.repositories.RoleRepository;
import com.graduation.youthtalentfund.repositories.UserRepository;
import com.graduation.youthtalentfund.utils.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPassword;
    @Value("${admin.fullname}")
    private String adminFullname;

    @Override
    public void run(String... args) throws Exception {

        if (roleRepository.count() == 0) {
            System.out.println("Database is empty. Initializing data...");

            // --- TẠO ROLES ---
            System.out.println("Initializing roles...");
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");

            Role staffRole = new Role();
            staffRole.setName("ROLE_STAFF");

            Role userRole = new Role();
            userRole.setName("ROLE_USER");

            roleRepository.saveAll(List.of(adminRole, staffRole, userRole));
            System.out.println("Roles initialized.");
        }

        // --- TẠO ADMIN   ---
        if (!userRepository.existsByEmail(adminEmail)) {
            System.out.println("Creating default admin account...");

            Role adminRoleEntity = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("FATAL: ROLE_ADMIN not found after initialization!"));

            User adminUser = new User();
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setFullName(adminFullname);
            adminUser.setStatus(UserStatus.ACTIVE);

            adminUser.setCode(CodeGenerator.generateUserCode());

            UserRole userRoleLink = new UserRole();
            userRoleLink.setUser(adminUser);
            userRoleLink.setRole(adminRoleEntity);

            adminUser.getUserRoles().add(userRoleLink);

            userRepository.save(adminUser);
            System.out.println("Default admin account created for email: " + adminEmail);
        }
    }
}