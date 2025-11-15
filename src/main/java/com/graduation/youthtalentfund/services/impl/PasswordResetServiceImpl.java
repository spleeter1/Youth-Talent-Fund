package com.graduation.youthtalentfund.services.impl;

import com.graduation.youthtalentfund.entities.User;
import com.graduation.youthtalentfund.exceptions.BadRequestException;
import com.graduation.youthtalentfund.exceptions.ResourceNotFoundException;
import com.graduation.youthtalentfund.repositories.UserRepository;
import com.graduation.youthtalentfund.services.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${frontend.reset-password-url}")
    private String frontendResetUrl;

    @Value("${spring.mail.username}")
    private String mailFrom;

    private static final int TOKEN_EXPIRY_MINUTES = 15;

    @Override
    @Transactional
    public void processForgotPassword(String userEmail){
        userRepository.findByEmail(userEmail).ifPresent(user -> {
            String token = UUID.randomUUID().toString();

            user.setResetPasswordToken(token);
            user.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES));

            userRepository.save(user);
            System.out.println(user.getEmail() + token);
            sendPasswordResetEmail(user.getEmail(),token);
        });
    }

    @Override
    @Transactional
    public void processResetPassword(String token, String newPassword){
        User user = validateResetToken(token);

        user.setPassword(passwordEncoder.encode(newPassword));

        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);

        userRepository.save(user);
    }

    @Override
    public User validateResetToken(String token){
        if(token == null || token.isBlank()){
            throw new BadRequestException("Token không hợp lệ");
        }

        User user = userRepository.findByResetPasswordToken(token).orElseThrow(() -> new ResourceNotFoundException("Token đặt lại mật khẩu không hợp lệ hoặc đã được sử dụng."));

        if(user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())){
            throw new BadRequestException("Token đặt lại mật khẩu đã hết hạn");
        }
        return user;
    }

    private void sendPasswordResetEmail(String toEmail, String token) {
        String resetUrl = frontendResetUrl + "?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(toEmail);
        message.setSubject("Yêu Cầu Đặt Lại Mật Khẩu");
        message.setText("Để đặt lại mật khẩu của bạn, vui lòng nhấn vào liên kết dưới đây:\n"
                + resetUrl + "\n\n"
                + "Nếu bạn không yêu cầu điều này, vui lòng bỏ qua email này. "
                + "Liên kết sẽ hết hạn sau " + TOKEN_EXPIRY_MINUTES + " phút.");

        mailSender.send(message);
    }
}
