package com.graduation.youthtalentfund.dtos.response.donate;

import lombok.Data;

@Data
public class DonationStatusResponse {
    private String status;
    private String code;
    private String transactionCode;
}
