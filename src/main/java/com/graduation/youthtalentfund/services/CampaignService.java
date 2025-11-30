package com.graduation.youthtalentfund.services;

import com.graduation.youthtalentfund.dtos.request.CreateCampaignDTO;
import com.graduation.youthtalentfund.entities.Campaign;
import com.graduation.youthtalentfund.repositories.Projection.CampaignDetailProjection;
import com.graduation.youthtalentfund.repositories.Projection.CampaignShortProjection;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface CampaignService {
    Page<CampaignShortProjection> searchCampaigns (String status, String category, String keyword, int page, int size);
    CampaignDetailProjection getByCodeOrSlug(String value);

    @Transactional
    Campaign createCampaign(CreateCampaignDTO request);
}
