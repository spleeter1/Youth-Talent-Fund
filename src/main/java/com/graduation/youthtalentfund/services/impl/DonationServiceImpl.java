package com.graduation.youthtalentfund.services.impl;

import com.graduation.youthtalentfund.dtos.DonationWebSocketDataMessage;
import com.graduation.youthtalentfund.dtos.PaymentWebSocketStatusMessage;
import com.graduation.youthtalentfund.dtos.request.donate.DonationCreateRequest;
import com.graduation.youthtalentfund.dtos.request.donate.DonationSearchRequest;
import com.graduation.youthtalentfund.dtos.request.donate.UserDonationStatisticRequest;
import com.graduation.youthtalentfund.dtos.response.donate.*;
import com.graduation.youthtalentfund.entities.Campaign;
import com.graduation.youthtalentfund.entities.CustomUserDetails;
import com.graduation.youthtalentfund.entities.Donation;
import com.graduation.youthtalentfund.entities.User;
import com.graduation.youthtalentfund.exceptions.ResourceNotFoundException;
import com.graduation.youthtalentfund.repositories.CampaignRepository;
import com.graduation.youthtalentfund.repositories.DonationRepository;
import com.graduation.youthtalentfund.repositories.Projection.DonationDataPublicProjection;
import com.graduation.youthtalentfund.repositories.Projection.TopDonatorProjection;
import com.graduation.youthtalentfund.repositories.Projection.TotalDonationStatisticProjection;
import com.graduation.youthtalentfund.repositories.UserRepository;
import com.graduation.youthtalentfund.services.DonationService;
import com.graduation.youthtalentfund.services.MailService;
import com.graduation.youthtalentfund.services.PayOsService;
import com.graduation.youthtalentfund.session.WebSocketTokenStore;
import com.graduation.youthtalentfund.utils.AuthUtil;
import com.graduation.youthtalentfund.utils.CodeGenerator;
import com.graduation.youthtalentfund.utils.mapper.DonationMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkStatus;
import vn.payos.model.webhooks.Webhook;
import vn.payos.model.webhooks.WebhookData;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;

    private final PayOsService payOsService;
    private final MailService mailService;
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketTokenStore wsTokenStore;
    private final Validator validator;

    public DonationCreateResponse createDonation(DonationCreateRequest donationCreateRequest) {
        Donation donation = new Donation();

        Optional<Campaign> campaignOptional = campaignRepository.findByCode(donationCreateRequest.getCampaignCode());
        if (campaignOptional.isEmpty())
            throw new ResourceNotFoundException("Campaign", "code", donationCreateRequest.getCampaignCode());
        Campaign campaign = campaignOptional.get();
        donation.setCampaign(campaign);

        CustomUserDetails customUserDetails = AuthUtil.getCurrentUser();
        if (customUserDetails != null) {
            Optional<User> userOptional = userRepository.findByCode(customUserDetails.getCode());
            userOptional.ifPresent(donation::setUser);
        }
        String donationCode = CodeGenerator.generateDonationCode();
        donation.setCode(donationCode);
        donation.setAmount(BigDecimal.valueOf(donationCreateRequest.getAmount()));
        donation.setDonorName(donationCreateRequest.getName());
        donation.setDonorEmail(donationCreateRequest.getEmail());
        donation.setDonorPhoneNumber(donationCreateRequest.getPhoneNumber());
        donation.setMessage(donationCreateRequest.getMessage());
        donation.setAnonymous(donationCreateRequest.isAnonymous());

        long orderCode = Instant.now().toEpochMilli(); // Dùng now() làm orderCode/transactionCode, tính theo ms nên tỉ lệ trùng rất thấp

        Map<String, Object> data = new HashMap<>();
        data.put("amount", donationCreateRequest.getAmount());
        data.put("cancelUrl", donationCreateRequest.getCancelUrl());
        data.put("description", donationCreateRequest.getCampaignCode());
        data.put("orderCode", orderCode);
        data.put("returnUrl", donationCreateRequest.getReturnUrl());

        CreatePaymentLinkRequest payOsRequest = CreatePaymentLinkRequest.builder()
                .amount(donationCreateRequest.getAmount())
                .orderCode(orderCode)
                .description(donationCreateRequest.getCampaignCode())
                .cancelUrl(donationCreateRequest.getCancelUrl())
                .returnUrl(donationCreateRequest.getReturnUrl())
                .signature(payOsService.createPaymentRequestSignature(data))
                .expiredAt(Instant.now().getEpochSecond() + 3600) //1h
                .build();

        CreatePaymentLinkResponse payOsResponse = payOsService.createPaymentLink(payOsRequest);

        donation.setTransactionCode(String.valueOf(payOsResponse.getOrderCode()));
        donation.setPaymentStatus(String.valueOf(payOsResponse.getStatus()));

        donationRepository.save(donation);

        if (donationCreateRequest.isSendMail()) {
            this.sendVerifyMail(donationCreateRequest.getEmail(), donationCreateRequest.getAmount(), donation.getTransactionCode(), campaign.getTitle(), campaign.getCode());
        }

        String wsToken = UUID.randomUUID().toString();

        wsTokenStore.put(wsToken, donationCode);

        DonationCreateResponse donationCreateResponse = new DonationCreateResponse();
        String qrCode = payOsResponse.getQrCode();
        String checkoutUrl = payOsResponse.getCheckoutUrl();
        donationCreateResponse.setQrCode(qrCode);
        donationCreateResponse.setCheckoutUrl(checkoutUrl);
        donationCreateResponse.setWsToken(wsToken);

        return donationCreateResponse;
    }

    @Scheduled(fixedDelay = 3600000) // mỗi 1h
    @Transactional
    public void cancelExpiredDonations() {

        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(60);

        List<Donation> expiredDonations = donationRepository.findExpiredPendingDonations(
                        PaymentLinkStatus.PENDING.getValue(),
                        expiredTime
                );

        for (Donation donation : expiredDonations) {
            donation.setPaymentStatus(PaymentLinkStatus.CANCELLED.getValue());
        }

        if (!expiredDonations.isEmpty()) {
            donationRepository.saveAll(expiredDonations);
        }
    }

    @Override
    public void handleWebhookData(Webhook hookData) {
        WebhookData webhookData = payOsService.getPayOS().webhooks().verify(hookData);
        String code = webhookData.getCode();
        System.out.println(webhookData);
        if (code.equalsIgnoreCase("00")) {
            String transactionCode = String.valueOf(webhookData.getOrderCode());

            Optional<Donation> donationOptional = donationRepository.findByTransactionCode(transactionCode);
            if (donationOptional.isEmpty()) throw new ResourceNotFoundException("Donation", "code", transactionCode);
            Donation donation = donationOptional.get();

            Campaign campaign = donation.getCampaign();
            BigDecimal current = campaign.getCurrentAmount();
            campaign.setCurrentAmount(current.add(donation.getAmount()));

            campaignRepository.save(campaign);

            donation.setPaymentStatus(PaymentLinkStatus.PAID.getValue());

            donationRepository.save(donation);

            notifyUserDonationSuccess(donation.getCode(), donation.getTransactionCode());

            DonationWebSocketDataMessage message = new DonationWebSocketDataMessage();
            message.setTime(donation.getUpdatedAt());
            message.setCampaignCode(donation.getCampaign().getCode());
            if (donation.isAnonymous()) {
                message.setDonorName(null);
            } else {
                message.setDonorName(donation.getDonorName());
            }
            message.setMessage(donation.getMessage());
            message.setAmount(donation.getAmount());

            broadcastDonation(message);

            sendThankMail(donation.getDonorEmail(), donation.getAmount().longValueExact(), donation.getTransactionCode(), campaign.getTitle(), campaign.getCode());
        }
    }

    @Override
    public void cancelDonation(String transactionCode) {
        Optional<Donation> donationOptional = donationRepository.findByTransactionCode(transactionCode);
        if (donationOptional.isEmpty()) throw new ResourceNotFoundException("Donation", "code", transactionCode);
        Donation donation = donationOptional.get();

        donation.setPaymentStatus(PaymentLinkStatus.CANCELLED.getValue());
        payOsService.getPayOS().paymentRequests().cancel(Long.valueOf(transactionCode));

        donationRepository.save(donation);
    }

    @Override
    public Page<DonationDataResponse> searchDonation(DonationSearchRequest request) {

        CustomUserDetails customUserDetails = AuthUtil.getCurrentUser();
        if (customUserDetails != null && !AuthUtil.isAdmin(customUserDetails)) {
            //Nếu không phải admin thì request mặc định dùng userCode. Ngược lại, admin có thể dùng bất kì userCode nào để lọc theo user
            request.setUserCode(customUserDetails.getCode());
        }

        Specification<Donation> donationSpecification = DonationMapper.toSpecification(request);
        Pageable pageable = Pageable.ofSize(20).withPage(request.getPageNumber());
        Page<Donation> donationPage = donationRepository.findAll(donationSpecification, pageable);
        return donationPage.map(DonationMapper::toResponseData);
    }

    @Override
    public DonationDataResponse getDonation(String donationCode) {
        Optional<Donation> donationOptional = donationRepository.findByCode(donationCode);
        if (donationOptional.isEmpty()) throw new ResourceNotFoundException("Donation", "code", donationCode);
        Donation donation = donationOptional.get();

        CustomUserDetails customUserDetails = AuthUtil.getCurrentUser();
        // Chỉ admin và chủ của donation đó được xem data
        if (customUserDetails != null && !donation.getUser().getCode().equals(customUserDetails.getCode()) && !AuthUtil.isAdmin(customUserDetails)) {
            throw new AccessDeniedException("Access denied");
        }

        return DonationMapper.toResponseData(donation);
    }

    @Override
    public Page<DonationDataPublicResponse> getDonationListPublicOfCampaign(String campaignCode, Integer page) {
        Page<DonationDataPublicProjection> projections = donationRepository.findDonationPublicListByCampaignCode(campaignCode, Pageable.ofSize(20).withPage(page));
        return projections.map(p -> {
            DonationDataPublicResponse response = new DonationDataPublicResponse();
            response.setTime(p.getTime());
            response.setAmount(p.getAmount());
            response.setDonorName(p.getDonorName());
            return response;
        });
    }

    @Override
    public Page<DonationDataPublicResponse> getRecentPublicDonationList() {
        Page<DonationDataPublicProjection> projections = donationRepository.getRecentPublicDonationList(Pageable.ofSize(10).withPage(0));
        return projections.map(p ->  {
            DonationDataPublicResponse response = new DonationDataPublicResponse();
            response.setTime(p.getTime());
            response.setAmount(p.getAmount());
            response.setDonorName(p.getDonorName());
            return response;
        });
    }

    @Override
    public DonationStatusResponse getDonationStatus(Long transactionCode) {
        Optional<Donation> donationOptional = donationRepository.findByTransactionCode(transactionCode.toString());
        if (donationOptional.isEmpty()) throw new ResourceNotFoundException("Donation", "transaction_code", transactionCode);
        Donation donation = donationOptional.get();

        DonationStatusResponse response = new DonationStatusResponse();
        response.setStatus(donation.getPaymentStatus());
        response.setCode(donation.getCode());
        response.setTransactionCode(donation.getTransactionCode());
        return response;
    }

    private void sendVerifyMail(String email, Long amount, String transactionCode, String campaignName, String campaignCode) {
        String text = "Xác nhận thông tin: \n" +
                "- Tên chiến dịch: " + campaignName + "\n" +
                "- Mã chiến dịch" + campaignCode + "\n" +
                "- Số tiền chuyển: " + amount + "\n" +
                "- Mã thanh toán: " + transactionCode;
        String subject = "Quỹ ủng hộ tài năng trẻ - Xác nhận đóng góp";
        mailService.sendMail(email, subject, text);

    }

    private void sendThankMail(String email, Long amount, String transactionCode, String campaignName, String campaignCode) {
        String text = "Cảm ơn bạn đã đóng góp! \n" +
                "- Tên chiến dịch: " + campaignName + "\n" +
                "- Mã chiến dịch" + campaignCode + "\n" +
                "- Số tiền chuyển: " + amount + "\n" +
                "- Mã thanh toán: " + transactionCode;
        String subject = "Quỹ ủng hộ tài năng trẻ - Chuyển khoản thành công";
        mailService.sendMail(email, subject, text);
    }

    @Override
    public Page<UserDonationStatisticResponse> getUserDonationStatistic(UserDonationStatisticRequest request) {
        CustomUserDetails customUserDetails = AuthUtil.getCurrentUser();
        // Chỉ admin được tìm kiếm theo userCode, user thường sẽ mặc định về userCode của họ
        if (customUserDetails != null && !AuthUtil.isAdmin(customUserDetails)) request.setUserCode(customUserDetails.getCode());
        Pageable pageable = Pageable.ofSize(20).withPage(request.getPageNumber());
        return donationRepository.getUserDonationStatistic(request.getUserCode(), request.getCampaignCode(), request.getFromDate(), request.getToDate(), pageable)
                .map(p -> {
                    UserDonationStatisticResponse r = new UserDonationStatisticResponse();
                    r.setUserCode(p.getUserCode());
                    r.setFullName(p.getFullName());
                    r.setPhoneNumber(p.getPhoneNumber());
                    r.setDonationCount(p.getDonationCount());
                    r.setCampaignCount(p.getCampaignCount());
                    r.setTotalDonated(p.getTotalDonated());
                    return r;
                });
    }

    @Override
    public TotalDonationStatisticResponse getTotalDonationStatistic(LocalDateTime start, LocalDateTime end) {
         TotalDonationStatisticProjection projection = donationRepository.getTotalDonationStatistic(start , end);
         TotalDonationStatisticResponse response = new TotalDonationStatisticResponse();
         response.setTotalDonation(projection.getTotalDonation());
         response.setGuestDonation(projection.getGuestDonation());
         response.setTotalReceived(projection.getTotalReceived());
         return response;
    }

    @Override
    public Page<TopDonatorResponse> getTopDonatorPublic() {
        Page<TopDonatorProjection> projections = donationRepository.findTopDonators(LocalDateTime.MIN, LocalDateTime.now(), Pageable.ofSize(10));
        return projections.map(p -> {
            TopDonatorResponse r = new TopDonatorResponse();
            r.setFullName(p.getFullName());
//            r.setPhoneNumber(p.getPhoneNumber()); No phoneNumber
            r.setAmount(p.getAmount());
            return r;
        });
    }

    @Override
    public Page<TopDonatorResponse> getTopDonatorStatistic(LocalDateTime start, LocalDateTime end, Integer page) {
        Page<TopDonatorProjection> projections = donationRepository.findTopDonators(LocalDateTime.MIN, LocalDateTime.now(), Pageable.ofSize(20).withPage(page));
        return projections.map(p -> {
            TopDonatorResponse r = new TopDonatorResponse();
            r.setFullName(p.getFullName());
            r.setPhoneNumber(p.getPhoneNumber());
            r.setAmount(p.getAmount());
            return r;
        });
    }

    // WebSocket
    @Override
    public void broadcastDonation(DonationWebSocketDataMessage message) {
        messagingTemplate.convertAndSend(
                "/topic/donations/" + message.getCampaignCode(),
                message
        );

        messagingTemplate.convertAndSend(
                "/topic/donations",
                message
        );
    }

    @Override
    public void notifyUserDonationSuccess(String donationCode, String orderCode) {

        messagingTemplate.convertAndSendToUser(
                donationCode, // user identifier
                "/queue/payment-status",
                new PaymentWebSocketStatusMessage("SUCCESS", orderCode)
        );
    }
}
