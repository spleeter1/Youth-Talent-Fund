// File: src/main/java/com/graduation/youthtalentfund/security/JwtTokenProvider.java
package com.graduation.youthtalentfund.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

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
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(userPrincipal.getUsername()) // Sử dụng email/username làm subject
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey()) // Ký với secret key
                .compact();
    }

    /**
     * Lấy username (email) từ chuỗi JWT.
     */
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey()) // Xác thực token bằng secret key
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
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