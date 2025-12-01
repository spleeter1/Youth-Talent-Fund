package com.graduation.youthtalentfund.repositories.Projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CampaignShortProjection {
    String getCategory();
    String getDurationsDays();
    String getTitle();
    String getDescription();
    BigDecimal getCurrentAmount();
    BigDecimal getTargetAmount();
    LocalDateTime getStartDate();
    LocalDateTime getEndDate();
    String getCoverImagePath();
    String getCode();
}
