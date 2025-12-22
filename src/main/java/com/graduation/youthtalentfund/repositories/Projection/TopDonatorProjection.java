package com.graduation.youthtalentfund.repositories.Projection;

import java.math.BigDecimal;

public interface TopDonatorProjection {
    String getFullName();
    String getPhoneNumber();
    BigDecimal getAmount();
}

