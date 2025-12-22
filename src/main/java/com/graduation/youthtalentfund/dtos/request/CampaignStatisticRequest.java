package com.graduation.youthtalentfund.dtos.request;

import com.graduation.youthtalentfund.enums.CampaignStatus;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CampaignStatisticRequest {
    private String campaignCode;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    @PositiveOrZero
    private Integer pageNumber;
}
