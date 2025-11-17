package com.graduation.youthtalentfund.services.impl;

import com.graduation.youthtalentfund.dtos.request.donate.DonationCreateRequest;
import com.graduation.youthtalentfund.dtos.request.donate.DonationSearchRequest;
import com.graduation.youthtalentfund.dtos.response.donate.DonationCreateResponse;
import com.graduation.youthtalentfund.dtos.response.donate.DonationDataResponse;
import com.graduation.youthtalentfund.entities.Campaign;
import com.graduation.youthtalentfund.entities.Donation;
import com.graduation.youthtalentfund.entities.User;
import com.graduation.youthtalentfund.exceptions.ResourceNotFoundException;
import com.graduation.youthtalentfund.repositories.CampaignRepository;
import com.graduation.youthtalentfund.repositories.DonationRepository;
import com.graduation.youthtalentfund.repositories.UserRepository;
import com.graduation.youthtalentfund.services.DonationService;
import com.graduation.youthtalentfund.services.MailService;
import com.graduation.youthtalentfund.services.PayOsService;
import com.graduation.youthtalentfund.utils.CodeGenerator;
import com.graduation.youthtalentfund.utils.mapper.DonationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkStatus;
import vn.payos.model.webhooks.Webhook;
import vn.payos.model.webhooks.WebhookData;

import java.math.BigDecimal;
import java.nio.file.attribute.UserPrincipal;
import java.time.Instant;
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

    public DonationCreateResponse createDonation(DonationCreateRequest donationCreateRequest) {
        Donation donation = new Donation();

        Optional<Campaign> campaignOptional = campaignRepository.findByCode(donationCreateRequest.getCampaignCode());
        if (campaignOptional.isEmpty()) throw new ResourceNotFoundException("Campaign", "code", donationCreateRequest.getCampaignCode());
        Campaign campaign = campaignOptional.get();
        donation.setCampaign(campaign);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            // User logged in
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserPrincipal userPrincipal) {
                Optional<User> userOptional = userRepository.findByEmail(userPrincipal.getName());
                userOptional.ifPresent(donation::setUser);
            }
        }

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            // User logged in
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserPrincipal userPrincipal) {
                Optional<User> userOptional = userRepository.findByEmail(userPrincipal.getName());
                userOptional.ifPresent(user -> request.setUserEmail(user.getCode()));
            }
        } else throw new AccessDeniedException("Must be logged in.");

        Specification<Donation> donationSpecification = DonationSearchRequest.buildSpecification(request);
        Pageable pageable = Pageable.ofSize(20).withPage(request.getPageNumber());
        Page<Donation> donationPage = donationRepository.findAll(donationSpecification, pageable);
        return donationPage.map(DonationMapper::toResponseData);
    }

    @Override
    public DonationDataResponse getDonation(String donationCode) {
        Optional<Donation> donationOptional = donationRepository.findByTransactionCode(donationCode);
        if (donationOptional.isEmpty()) throw new ResourceNotFoundException("Donation", "code", donationCode);
        Donation donation = donationOptional.get();

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


}
