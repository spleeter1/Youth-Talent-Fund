package com.graduation.youthtalentfund.dtos.response.donate;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TotalDonationStatisticResponse {
    private BigDecimal totalReceived;
    private Long totalDonation;
    private Long guestDonation;
}
