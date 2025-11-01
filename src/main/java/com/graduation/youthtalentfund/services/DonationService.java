package com.graduation.youthtalentfund.services;

import com.graduation.youthtalentfund.dtos.request.donate.DonationCreateRequest;
import com.graduation.youthtalentfund.dtos.response.donate.DonationCreateResponse;
import vn.payos.model.webhooks.Webhook;

public interface DonationService {
    /**
     * Tạo qrCode, và gửi mail cho user nếu cần
     * @param donationCreateRequest Donation info schema
     * @return QRCode và link PayOS
     */
    DonationCreateResponse createDonation(DonationCreateRequest donationCreateRequest);

    /**
     * Xử lí data payment từ PayOS (update Status, v.v...)
     * @param hookData data payment từ PayOS
     */
    void handleWebhookData(Webhook hookData);
}
