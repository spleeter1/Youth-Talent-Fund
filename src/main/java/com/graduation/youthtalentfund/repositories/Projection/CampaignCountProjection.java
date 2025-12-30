package com.graduation.youthtalentfund.repositories.Projection;

public interface CampaignCountProjection {
    Long getActiveCampaign();
    Long getFinishedCampaign();
    Long getTotalCampaign();
}
