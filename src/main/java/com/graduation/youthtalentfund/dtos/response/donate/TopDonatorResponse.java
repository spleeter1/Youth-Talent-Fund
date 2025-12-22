package com.graduation.youthtalentfund.dtos.response.donate;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TopDonatorResponse {
    private String fullName;
    private String phoneNumber;
    private BigDecimal amount;
}
