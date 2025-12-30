package com.graduation.youthtalentfund.dtos.request.donate;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDonationStatisticRequest {
    @NotEmpty
    private String userCode;
    private String campaignCode;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    @PositiveOrZero
    private Integer pageNumber;
}
