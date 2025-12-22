package com.graduation.youthtalentfund.repositories.Projection;

import java.math.BigDecimal;

public interface UserDonationStatisticProjection {

    String getUserCode();
    String getPhoneNumber();
    String getFullName();
    Long getDonationCount();
    Long getCampaignCount();
    BigDecimal getTotalDonated();
}

