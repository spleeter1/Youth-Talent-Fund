package com.graduation.youthtalentfund.repositories.Projection;

import java.time.LocalDateTime;

public interface StaffProjection {
    String getFullName();
    String getEmail();
    String getPhoneNumber();
    String getAddress();
    String getAvatarPath();
    String getBio();
    String getCode();
    String getStatus();
    LocalDateTime getCreatedAt();
    Long getTotalInProgress();
    Long getTotalCompleted();
    Long getTotalDonations();
}

