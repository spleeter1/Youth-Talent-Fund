package com.graduation.youthtalentfund.repositories.Projection;

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
    BigDecimal getCurrentAmount();
    BigDecimal getTargetAmount();
    String getStatus();
    String getDurationDays();

    String getStaffCode();
    String getStaffName();
    String getStaffEmail();
}
