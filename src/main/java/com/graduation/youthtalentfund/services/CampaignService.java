package com.graduation.youthtalentfund.services;

import com.graduation.youthtalentfund.dtos.request.CampaignStatisticRequest;
import com.graduation.youthtalentfund.dtos.request.CreateCampaignDTO;
import com.graduation.youthtalentfund.dtos.request.UpdateCampaignDTO;
import com.graduation.youthtalentfund.dtos.response.CampaignCountResponse;
import com.graduation.youthtalentfund.dtos.response.CampaignDetailDTO;
import com.graduation.youthtalentfund.dtos.response.CampaignStatisticResponse;
import com.graduation.youthtalentfund.entities.Campaign;
import com.graduation.youthtalentfund.entities.CustomUserDetails;
import com.graduation.youthtalentfund.enums.CampaignStatus;
import com.graduation.youthtalentfund.repositories.Projection.CampaignDetailProjection;
import com.graduation.youthtalentfund.repositories.Projection.CampaignShortProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public interface CampaignService {
    Page<CampaignShortProjection> searchCampaigns(String status, String category, String keyword, int page, int size);

    CampaignDetailProjection getByCodeOrSlug(String value);

    CampaignDetailDTO createCampaign(CreateCampaignDTO request, MultipartFile image);

    CampaignStatus determineStatus(Campaign campaign);

    CampaignDetailDTO updateCampaign(String campaignCode, UpdateCampaignDTO updateCampaignDTO, MultipartFile image);

    CampaignDetailDTO updateCampaignStatus(String code, CampaignStatus newStatus);

    Page<CampaignStatisticResponse> getCampaignStatistic(CampaignStatisticRequest request);

    CampaignCountResponse getCampaignCount(LocalDateTime fromDate, LocalDateTime toDate);

    Page<CampaignShortProjection> getMyCampaigns(
            CustomUserDetails userDetails,
            String status,
            String category,
            String keyword,
            Pageable pageable);

    Page<CampaignShortProjection> getCampaignsByStaffId(
            String staffCode,
            String status,
            String category,
            String keyword,
            Pageable pageable);
}
