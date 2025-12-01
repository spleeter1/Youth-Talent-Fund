// File: src/main/java/com/graduation/youthtalentfund/security/JwtTokenProvider.java
package com.graduation.youthtalentfund.security;

import com.graduation.youthtalentfund.entities.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Tạo JWT từ đối tượng Authentication của Spring Security.
     */
    public String generateToken(Authentication authentication) {
        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(auth -> auth.startsWith("ROLE_") ? auth.substring(5) : auth)
                .toList();

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("code", userPrincipal.getCode())
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey()) // Ký với secret key
                .compact();
    }

    /**
     * Parse và lấy value
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    public String getCodeFromJWT(String token) {
        Claims claims = parseClaims(token);
        return claims.get("code", String.class);
    }

    public List<String> extractRoles(String token) {
        Claims claims = parseClaims(token);
        Object rolesObj = claims.get("roles");
        if (rolesObj == null) return null;
        @SuppressWarnings("unchecked")
        List<Object> raw = (List<Object>) rolesObj;
        return raw.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    /**
     * Kiểm tra tính hợp lệ của token.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            // Ghi log lỗi để debug
            logger.error("Invalid JWT token: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Chuyển đổi chuỗi secret (được mã hóa Base64) thành một đối tượng SecretKey.
     * Đây là phương thức cốt lõi để làm việc với thuật toán HMAC-SHA.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}