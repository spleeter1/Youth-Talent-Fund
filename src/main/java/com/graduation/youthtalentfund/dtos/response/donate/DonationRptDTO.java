package com.graduation.youthtalentfund.dtos.response.donate;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DonationRptDTO {
    private String code;
    private BigDecimal amount;
    private String donorName;
    private String donorEmail;
    private String donorPhoneNumber;
    private String message;
    private boolean isAnonymous;
    private String transactionCode;
    private String paymentStatus;
}
