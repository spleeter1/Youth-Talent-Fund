package com.graduation.youthtalentfund.controllers;

import com.graduation.youthtalentfund.dtos.request.donate.DonationCancelRequest;
import com.graduation.youthtalentfund.dtos.request.donate.DonationSearchRequest;
import com.graduation.youthtalentfund.dtos.request.donate.UserDonationStatisticRequest;
import com.graduation.youthtalentfund.dtos.response.donate.DonationDataResponse;
import com.graduation.youthtalentfund.dtos.response.donate.UserDonationStatisticResponse;
import com.graduation.youthtalentfund.services.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/donation")
@RequiredArgsConstructor
public class UserDonationController {
    private final DonationService donationService;

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelDonation(@Valid @RequestBody DonationCancelRequest cancelRequest) {
        donationService.cancelDonation(cancelRequest.getTransactionCode().toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/history")
    public ResponseEntity<Page<DonationDataResponse>> listDonations(@Valid @RequestBody DonationSearchRequest request) {
        Page<DonationDataResponse> donationPage = donationService.searchDonation(request);
        return ResponseEntity.ok(donationPage);
    }

    @GetMapping("/history")
    public ResponseEntity<DonationDataResponse> getDonation(@Valid @RequestParam String code) {
        DonationDataResponse donationDataResponse = donationService.getDonation(code);
        return ResponseEntity.ok(donationDataResponse);
    }

    @PostMapping("/statistic/user")
    public ResponseEntity<Page<UserDonationStatisticResponse>> getUserDonationStatistic(@Valid @RequestBody UserDonationStatisticRequest request) {
        Page<UserDonationStatisticResponse> userDonationStatResponses = donationService.getUserDonationStatistic(request);
        return ResponseEntity.ok(userDonationStatResponses);
    }
}
