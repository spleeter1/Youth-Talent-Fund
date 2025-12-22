package com.graduation.youthtalentfund.utils;

import com.graduation.youthtalentfund.entities.CustomUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
    public static CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Must be logged in.");
        }

        if (auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails;
        }
        throw new AccessDeniedException("Unauthorized");
    }

    public static boolean isAdmin(CustomUserDetails customUserDetails) {
        return customUserDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public static boolean isAdminOrStaff(CustomUserDetails customUserDetails) {
        return customUserDetails.getAuthorities().stream()
                .anyMatch(a -> {
                    String authority = a.getAuthority();
                    return authority.equals("ROLE_ADMIN") || authority.equals("ROLE_STAFF");
                });
    }
}
