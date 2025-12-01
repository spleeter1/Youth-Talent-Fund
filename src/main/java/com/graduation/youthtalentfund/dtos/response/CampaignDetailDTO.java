package com.graduation.youthtalentfund.dtos.response;

import com.graduation.youthtalentfund.enums.CampaignCategory;
import com.graduation.youthtalentfund.enums.CampaignStatus;
import com.graduation.youthtalentfund.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CampaignDetailDTO {
    private String code;
    private String slug;
    private String title;
    private String description;
    private String location;
    private String story;

    private BigDecimal targetAmount;
    private BigDecimal currentAmount;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;

    private CampaignCategory category;
    private CampaignStatus status;

    private ImageResponseDTO coverImage;

    private StaffInfoDTO assignee;

    @Data
    @Builder
    public static class StaffInfoDTO {
        private String fullName;
        private String code;
        private String email;
        private ImageResponseDTO avatar;
        private UserStatus status;
    }
}
