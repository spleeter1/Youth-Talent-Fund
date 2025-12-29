package com.graduation.youthtalentfund.repositories.Projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface DonationDataPublicProjection {

    String getDonorName();

    BigDecimal getAmount();

    LocalDateTime getTime();
}

