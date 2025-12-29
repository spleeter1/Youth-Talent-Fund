package com.graduation.youthtalentfund.services;

import com.graduation.youthtalentfund.dtos.DonationWebSocketDataMessage;
import com.graduation.youthtalentfund.dtos.request.donate.DonationCreateRequest;
import com.graduation.youthtalentfund.dtos.request.donate.DonationSearchRequest;
import com.graduation.youthtalentfund.dtos.request.donate.UserDonationStatisticRequest;
import com.graduation.youthtalentfund.dtos.response.donate.*;
import org.springframework.data.domain.Page;
import vn.payos.model.webhooks.Webhook;

import java.time.LocalDateTime;

public interface DonationService {
    /**
     * Tạo qrCode, và gửi mail cho user nếu cần
     * @param donationCreateRequest Donation info schema
     * @return QRCode và link PayOS
     */
    DonationCreateResponse createDonation(DonationCreateRequest donationCreateRequest);

    /**
     * Hủy link/qr donation, và set PaymentStatus của Donation thành CANCELLED
     * @param transactionCode Transaction code của donation
     */
    void cancelDonation(String transactionCode);

    /**
     * Xử lí data payment từ PayOS (update Status, v.v...)
     * @param hookData data payment từ PayOS
     */
    void handleWebhookData(Webhook hookData);

    Page<DonationDataResponse> searchDonation(DonationSearchRequest request);

    DonationDataResponse getDonation(String donationCode);

    Page<UserDonationStatisticResponse> getUserDonationStatistic(UserDonationStatisticRequest request);

    Page<TopDonatorResponse> getTopDonatorPublic();

    Page<TopDonatorResponse> getTopDonatorStatistic(LocalDateTime start, LocalDateTime end, Integer page);

    TotalDonationStatisticResponse getTotalDonationStatistic(LocalDateTime start, LocalDateTime end);

    DonationStatusResponse getDonationStatus(Long transactionCode);

    Page<DonationDataPublicResponse> getDonationListPublicOfCampaign(String campaignCode, Integer page);

    Page<DonationDataPublicResponse> getRecentPublicDonationList();
    // WebSocket
    /**
     * WebSocket để post thông báo có người thanh toán thành công
     * @param message Message thông báo
     */
    void broadcastDonation(DonationWebSocketDataMessage message);

    /**
     * user/queue/payment-status <br>
     * WebSocket dùng để điều hướng trình duyệt người dùng sau khi thanh toán QR thành công, yêu cầu wsToken khi connect
     */
    void notifyUserDonationSuccess(String donationCode, String orderCode);
}
