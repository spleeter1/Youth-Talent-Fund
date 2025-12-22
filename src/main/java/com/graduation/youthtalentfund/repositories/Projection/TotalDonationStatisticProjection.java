package com.graduation.youthtalentfund.repositories.Projection;

import java.math.BigDecimal;

public interface TotalDonationStatisticProjection {
    BigDecimal getTotalReceived();
    Long getTotalDonation();
    Long getGuestDonation();
}

