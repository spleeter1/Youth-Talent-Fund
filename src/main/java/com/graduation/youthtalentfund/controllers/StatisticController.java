package com.graduation.youthtalentfund.controllers;

import com.graduation.youthtalentfund.dtos.request.CampaignStatisticRequest;
import com.graduation.youthtalentfund.dtos.request.donate.UserDonationStatisticRequest;
import com.graduation.youthtalentfund.dtos.response.CampaignStatisticResponse;
import com.graduation.youthtalentfund.dtos.response.donate.TopDonatorResponse;
import com.graduation.youthtalentfund.dtos.response.donate.TotalDonationStatisticResponse;
import com.graduation.youthtalentfund.dtos.response.donate.UserDonationStatisticResponse;
import com.graduation.youthtalentfund.services.CampaignService;
import com.graduation.youthtalentfund.services.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/management/statistic")
@RequiredArgsConstructor
public class StatisticController {

    private final DonationService donationService;
    private final CampaignService campaignService;

    @GetMapping("/top-donator")
    public ResponseEntity<Page<TopDonatorResponse>> getTopDonatorStatistic(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end,
            @RequestParam(defaultValue = "0") Integer page
    ) {
        Page<TopDonatorResponse> responses = donationService.getTopDonatorStatistic(start, end, page);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/total-donation")
    public ResponseEntity<TotalDonationStatisticResponse> getTotalDonationStatistic(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end
    ) {
        TotalDonationStatisticResponse response = donationService.getTotalDonationStatistic(start , end);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/donation/user")
    public ResponseEntity<Page<UserDonationStatisticResponse>> getUserDonationStatistic(@Valid @RequestBody UserDonationStatisticRequest request) {
        Page<UserDonationStatisticResponse> userDonationStatResponses = donationService.getUserDonationStatistic(request);
        return ResponseEntity.ok(userDonationStatResponses);
    }

    @PostMapping("/campaign/statistic")
    public ResponseEntity<Page<CampaignStatisticResponse>> getCampaignStatistic(@Valid @RequestBody CampaignStatisticRequest request) {
        Page<CampaignStatisticResponse> responses = campaignService.getCampaignStatistic(request);
        return ResponseEntity.ok(responses);
    }

}
