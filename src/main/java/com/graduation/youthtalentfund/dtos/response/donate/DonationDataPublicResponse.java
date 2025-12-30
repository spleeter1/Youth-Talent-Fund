package com.graduation.youthtalentfund.dtos.response.donate;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DonationDataPublicResponse {
    private String donorName;
    private BigDecimal amount;
    private LocalDateTime time;
}
