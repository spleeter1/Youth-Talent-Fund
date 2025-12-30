package com.graduation.youthtalentfund.repositories.Projection;

import com.graduation.youthtalentfund.enums.CampaignStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CampaignDetailProjection {
    String getCode();
    String getSlug();
    String getTitle();
    String getCoverImagePath();
    String getDescription();
    String getStory();
    String getLocation();
    LocalDateTime getStartDate();
    LocalDateTime getEndDate();
    String getCategory();
    BigDecimal getCurrentAmount();
    BigDecimal getTargetAmount();
    String getStatus();
    String getDurationDays();

    String getStaffCode();
    String getStaffName();
    String getStaffEmail();
}
