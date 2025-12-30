package com.graduation.youthtalentfund.dtos.response;

import lombok.Data;

@Data
public class CampaignCountResponse {
    private Long activeCampaign;
    private Long finishedCampaign;
    private Long totalCampaign;
}
