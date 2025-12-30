package com.graduation.youthtalentfund.dtos.response.donate;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserDonationStatisticResponse {
    private String userCode;
    private String fullName;
    private String phoneNumber;
    private Long donationCount;
    private Long campaignCount;
    private BigDecimal totalDonated;
}
