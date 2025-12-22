package com.graduation.youthtalentfund.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CampaignStatisticResponse {
    private String campaignCode;
    private String title;
    private String staffCode;
    private Long donationCount;
    private BigDecimal totalReceived;
}
