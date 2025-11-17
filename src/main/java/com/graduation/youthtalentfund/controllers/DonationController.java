package com.graduation.youthtalentfund.controllers;

import com.graduation.youthtalentfund.dtos.request.donate.DonationCreateRequest;
import com.graduation.youthtalentfund.dtos.response.donate.DonationCreateResponse;
import com.graduation.youthtalentfund.services.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.model.webhooks.Webhook;

@RestController
@RequestMapping("/api/public/donate")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @PostMapping("/create")
    public ResponseEntity<?> createDonation(@Valid @RequestBody DonationCreateRequest donationCreateRequest) {
        DonationCreateResponse response = donationService.createDonation(donationCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/hook")
    public ResponseEntity<?> handleWebhookData(@Valid @RequestBody Webhook hookData) {

        donationService.handleWebhookData(hookData);

        return ResponseEntity.status(200).body(hookData); // PayOS want 200 return
    }

    @GetMapping("/")
    public String testDonate() {
        return "Hello :>";
    }
}
