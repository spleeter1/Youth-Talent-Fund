package com.graduation.youthtalentfund.services.impl;

import com.graduation.youthtalentfund.entities.CustomUserDetails;
import com.graduation.youthtalentfund.entities.User;
import com.graduation.youthtalentfund.enums.UserStatus;
import com.graduation.youthtalentfund.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmail(username)
                .orElse(null);

        if (user == null) {
            user = userRepository.findByCode(username)
                    .orElseThrow(() ->
                            new UsernameNotFoundException("User not found with email or code: " + username)
                    );
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new DisabledException("User is disabled/deleted");
        }

        return new CustomUserDetails(user);
    }
}
