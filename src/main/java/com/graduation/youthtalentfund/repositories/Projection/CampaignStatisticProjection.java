package com.graduation.youthtalentfund.repositories.Projection;

import java.math.BigDecimal;

public interface CampaignStatisticProjection {
    String getCampaignCode();
    String getTitle();
    String getStaffCode();
    Long getDonationCount();
    BigDecimal getTotalReceived();
}

