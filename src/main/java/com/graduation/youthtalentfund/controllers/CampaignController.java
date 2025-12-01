package com.graduation.youthtalentfund.controllers;

import com.graduation.youthtalentfund.dtos.request.CreateCampaignDTO;
import com.graduation.youthtalentfund.dtos.response.CampaignDetailDTO;
import com.graduation.youthtalentfund.entities.Campaign;
import com.graduation.youthtalentfund.repositories.Projection.CampaignDetailProjection;
import com.graduation.youthtalentfund.repositories.Projection.CampaignShortProjection;
import com.graduation.youthtalentfund.services.CampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CampaignController {

    private final CampaignService campaignService;

    @GetMapping("/public/campaigns")
    public ResponseEntity<Page<CampaignShortProjection>> searchCampaigns(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CampaignShortProjection> campaignPage = campaignService.searchCampaigns(status, category, keyword, page, size);

        return ResponseEntity.ok(campaignPage);
    }

    @GetMapping("/public/campaigns/detail")
    public ResponseEntity<CampaignDetailProjection> getDetail(@RequestParam("value") String value) {
        return ResponseEntity.ok(campaignService.getByCodeOrSlug(value));
    }

    // admin
    @PostMapping(value="/management/campaign", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CampaignDetailDTO> createCampaign(@RequestPart("data") @Valid CreateCampaignDTO request,
                                            @RequestPart(value = "image", required = false) MultipartFile image) {
        CampaignDetailDTO newCampaign = campaignService.createCampaign(request, image);
        return ResponseEntity.ok(newCampaign);
    }
}
