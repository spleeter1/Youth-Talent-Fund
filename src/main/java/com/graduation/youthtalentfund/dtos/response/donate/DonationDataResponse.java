package com.graduation.youthtalentfund.dtos.response.donate;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DonationDataResponse {

    private String code;

    private Long amount;

    private String donorName;

    private String donorEmail;

    private String donorPhoneNumber;

    private String message;

    private boolean isAnonymous;

    private String transactionCode;

    private String paymentStatus;

    private String campaignCode;

    private String userCode;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
