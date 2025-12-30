package com.graduation.youthtalentfund.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DonationWebSocketDataMessage {
    private String campaignCode;
    private BigDecimal amount;
    private String donorName;
    private String message;
//    private BigDecimal totalCampaignAmount;
    private LocalDateTime time;
}

