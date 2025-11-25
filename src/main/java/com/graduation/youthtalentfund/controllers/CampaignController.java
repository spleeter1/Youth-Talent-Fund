package com.graduation.youthtalentfund.controllers;

import com.graduation.youthtalentfund.repositories.Projection.CampaignShortProjection;
import com.graduation.youthtalentfund.services.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/campaigns")
public class CampaignController {

    private final CampaignService campaignService;

    @GetMapping
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
}
