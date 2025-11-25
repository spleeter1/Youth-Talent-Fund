package com.graduation.youthtalentfund.services.impl;

import com.graduation.youthtalentfund.exceptions.ResourceNotFoundException;
import com.graduation.youthtalentfund.repositories.CampaignRepository;
import com.graduation.youthtalentfund.repositories.Projection.CampaignDetailProjection;
import com.graduation.youthtalentfund.repositories.Projection.CampaignShortProjection;
import com.graduation.youthtalentfund.services.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
    private final CampaignRepository campaignRepository;

    @Override
    public Page<CampaignShortProjection> searchCampaigns(String status, String category, String keyword, int page, int size) {
        return campaignRepository.findAllCampaignsShort(status, category, keyword, PageRequest.of(page, size));
    }

    @Override
    public CampaignDetailProjection getByCodeOrSlug(String value) {
        return campaignRepository.findByCodeOrSlug(value).orElseThrow(() -> new ResourceNotFoundException("Campaign Not Found"));
    }
}
