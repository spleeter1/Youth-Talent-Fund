package com.graduation.youthtalentfund.repositories.Projection;

import java.time.LocalDateTime;

public interface StaffDetailProjection {
    String getFullName();
    String getCode();
    String getEmail();
    LocalDateTime getCreatedAt();
    String getAvatarPath();
    String getStatus();
    String getAddress();
    String getPhoneNumber();
    String getBio();
}
