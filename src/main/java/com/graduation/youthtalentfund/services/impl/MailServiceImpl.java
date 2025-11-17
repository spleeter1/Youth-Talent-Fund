package com.graduation.youthtalentfund.services.impl;

import com.graduation.youthtalentfund.services.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    @Value("${spring.mail.username}")
    private String hostMail;

    private final JavaMailSender javaMailSender;

    public void sendMail(String toEmail, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(hostMail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);


        javaMailSender.send(message);

    }
}
