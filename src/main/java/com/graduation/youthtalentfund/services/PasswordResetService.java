package com.graduation.youthtalentfund.services;

import com.graduation.youthtalentfund.entities.User;

public interface PasswordResetService {
    void processForgotPassword(String userEmail);
    void processResetPassword(String token,String newPassword);
    User validateResetToken(String token);
}
