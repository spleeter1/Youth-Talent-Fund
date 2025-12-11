package com.graduation.youthtalentfund.services;

public interface MailService {
    void sendMail(String toEmail, String subject, String text);
}
