package com.graduation.youthtalentfund.controllers;

import com.graduation.youthtalentfund.dtos.request.donate.DonationCreateRequest;
import com.graduation.youthtalentfund.dtos.response.donate.DonationCreateResponse;
import com.graduation.youthtalentfund.exceptions.ResourceNotFoundException;
import com.graduation.youthtalentfund.services.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.model.webhooks.Webhook;

import java.util.Map;

@RestController
@RequestMapping("/api/public/donate")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @PostMapping("/create")
    public ResponseEntity<?> createDonation(@RequestBody DonationCreateRequest donationCreateRequest) {
        try {
            DonationCreateResponse response = donationService.createDonation(donationCreateRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException re) return ResponseEntity.badRequest().body(re.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("message", "Error creating donation"));
        }

    }

    @PostMapping("/webhook")
    public void handleWebhookData(@RequestBody Webhook hookData) {
        // Không có ResponseBody vì endpoint này chỉ để nhận data từ payOS
        donationService.handleWebhookData(hookData);
    }

    @GetMapping("/")
    public String testDonate() {
        return "Hello :>";
    }
}
