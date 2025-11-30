package com.graduation.youthtalentfund.dtos.request;

import com.graduation.youthtalentfund.enums.CampaignCategory;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateCampaignDTO {
    private String title;
    private String description;
    private String location;
    private String story;
    private BigDecimal targetAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @NotNull(message = "Category is required")
    private CampaignCategory category;
    private String assigneeCode;
}
