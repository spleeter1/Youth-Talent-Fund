package com.graduation.youthtalentfund.entities;

import com.graduation.youthtalentfund.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public String getCode() {
        return user.getCode();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getFullName() {
        return user.getFullName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getUserRoles() == null || user.getUserRoles().isEmpty()) {
            return List.of();
        }

        return user.getUserRoles().stream()
                .map(UserRole::getRole)
                .filter(Objects::nonNull)
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
                .toList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() != UserStatus.LOCK && user.getStatus() != UserStatus.DELETE;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == UserStatus.ACTIVE;
    }
}
