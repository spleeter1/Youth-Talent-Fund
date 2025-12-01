package com.graduation.youthtalentfund.services;

import com.graduation.youthtalentfund.repositories.Projection.CampaignDetailProjection;
import com.graduation.youthtalentfund.repositories.Projection.CampaignShortProjection;
import org.springframework.data.domain.Page;

public interface CampaignService {
    Page<CampaignShortProjection> searchCampaigns (String status, String category, String keyword, int page, int size);
    CampaignDetailProjection getByCodeOrSlug(String value);
}
