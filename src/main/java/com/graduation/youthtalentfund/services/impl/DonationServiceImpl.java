package com.graduation.youthtalentfund.services.impl;

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
import com.graduation.youthtalentfund.repositories.Projection.TopDonatorProjection;
import com.graduation.youthtalentfund.repositories.Projection.TotalDonationStatisticProjection;
import com.graduation.youthtalentfund.repositories.UserRepository;
import com.graduation.youthtalentfund.services.DonationService;
import com.graduation.youthtalentfund.services.MailService;
import com.graduation.youthtalentfund.services.PayOsService;
import com.graduation.youthtalentfund.utils.AuthUtil;
import com.graduation.youthtalentfund.utils.CodeGenerator;
import com.graduation.youthtalentfund.utils.mapper.DonationMapper;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;

    private final PayOsService payOsService;
    private final MailService mailService;
    private final Validator validator;

    public DonationCreateResponse createDonation(DonationCreateRequest donationCreateRequest) {
        Donation donation = new Donation();

        Optional<Campaign> campaignOptional = campaignRepository.findByCode(donationCreateRequest.getCampaignCode());
        if (campaignOptional.isEmpty())
            throw new ResourceNotFoundException("Campaign", "code", donationCreateRequest.getCampaignCode());
        Campaign campaign = campaignOptional.get();
        donation.setCampaign(campaign);

        CustomUserDetails customUserDetails = AuthUtil.getCurrentUser();
        Optional<User> userOptional = userRepository.findByCode(customUserDetails.getCode());
        userOptional.ifPresent(donation::setUser);

        donation.setCode(CodeGenerator.generateDonationCode());
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
            this.sendMail(donationCreateRequest.getEmail(), donationCreateRequest.getAmount(), donation.getTransactionCode());
        }

        DonationCreateResponse donationCreateResponse = new DonationCreateResponse();
        String qrCode = payOsResponse.getQrCode();
        String checkoutUrl = payOsResponse.getQrCode();
        donationCreateResponse.setQrCode(qrCode);
        donationCreateResponse.setCheckoutUrl(checkoutUrl);
        donationCreateResponse.setSignature(payOsService.createPaymentRequestSignature(Map.of("qrCode", qrCode, "checkoutUrl", checkoutUrl)));
        return donationCreateResponse;
    }

    @Override
    public void handleWebhookData(Webhook hookData) {
        WebhookData webhookData = payOsService.getPayOS().webhooks().verify(hookData);
        String code = webhookData.getCode();
        if (code.equalsIgnoreCase("00")) {
            String transactionCode = String.valueOf(webhookData.getOrderCode());

            finishDonation(transactionCode);
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
        if (!AuthUtil.isAdmin(customUserDetails)) {
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
        Optional<Donation> donationOptional = donationRepository.findByTransactionCode(donationCode);
        if (donationOptional.isEmpty()) throw new ResourceNotFoundException("Donation", "code", donationCode);
        Donation donation = donationOptional.get();

        CustomUserDetails customUserDetails = AuthUtil.getCurrentUser();
        // Chỉ admin và chủ của donation đó được xem data
        if (!donation.getUser().getCode().equals(customUserDetails.getCode()) && !AuthUtil.isAdmin(customUserDetails)) {
            throw new AccessDeniedException("Access denied");
        }

        return DonationMapper.toResponseData(donation);
    }

    private void finishDonation(String transactionCode) {
        Optional<Donation> donationOptional = donationRepository.findByTransactionCode(transactionCode);
        if (donationOptional.isEmpty()) throw new ResourceNotFoundException("Donation", "code", transactionCode);
        Donation donation = donationOptional.get();

        donation.setPaymentStatus(PaymentLinkStatus.PAID.getValue());

        donationRepository.save(donation);
    }

    private void sendMail(String email, Long amount, String transactionCode) {
        String text = "Xác nhận thông tin: \n" +
                "- Số tiền chuyển: " + amount + "\n" +
                "- Mã thanh toán: " + transactionCode;
        String subject = "Quỹ ủng hộ tài năng trẻ";
        mailService.sendMail(email, subject, text);

    }

    @Override
    public Page<UserDonationStatisticResponse> getUserDonationStatistic(UserDonationStatisticRequest request) {
        CustomUserDetails customUserDetails = AuthUtil.getCurrentUser();
        // Chỉ admin được tìm kiếm theo userCode, user thường sẽ mặc định về userCode của họ
        if (!AuthUtil.isAdmin(customUserDetails)) request.setUserCode(customUserDetails.getCode());
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
}
