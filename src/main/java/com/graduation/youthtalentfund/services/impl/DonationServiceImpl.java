package com.graduation.youthtalentfund.services.impl;

import com.graduation.youthtalentfund.dtos.request.donate.DonationCreateRequest;
import com.graduation.youthtalentfund.dtos.response.donate.DonationCreateResponse;
import com.graduation.youthtalentfund.entities.Campaign;
import com.graduation.youthtalentfund.entities.Donation;
import com.graduation.youthtalentfund.entities.User;
import com.graduation.youthtalentfund.exceptions.ResourceNotFoundException;
import com.graduation.youthtalentfund.repositories.CampaignRepository;
import com.graduation.youthtalentfund.repositories.DonationRepository;
import com.graduation.youthtalentfund.repositories.UserRepository;
import com.graduation.youthtalentfund.services.DonationService;
import com.graduation.youthtalentfund.services.PayOsService;
import com.graduation.youthtalentfund.utils.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkStatus;
import vn.payos.model.webhooks.Webhook;
import vn.payos.model.webhooks.WebhookData;

import java.math.BigDecimal;
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
    private final JavaMailSender mailSender;

    private final PayOsService payOsService;

    @Value("${spring.mail.username}")
    private String hostMail;

    public DonationCreateResponse createDonation(DonationCreateRequest donationCreateRequest) {
        Donation donation = new Donation();

        Optional<Campaign> campaignOptional = campaignRepository.findByCode(donationCreateRequest.getCampaignCode());
        if (campaignOptional.isEmpty()) throw new ResourceNotFoundException("Campaign", "code", donationCreateRequest.getCampaignCode());
        Campaign campaign = campaignOptional.get();
        donation.setCampaign(campaign);

        String userCode = donationCreateRequest.getUserCode();
        if (userCode != null && !userCode.isBlank()) {
            Optional<User> userOptional = userRepository.findByCode(donationCreateRequest.getUserCode());
            if (userOptional.isEmpty()) throw new ResourceNotFoundException("User", "code", donationCreateRequest.getUserCode());
            User user = userOptional.get();
            donation.setUser(user);
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
                .build();

        CreatePaymentLinkResponse payOsResponse = payOsService.createPaymentLink(payOsRequest);

        donation.setTransactionCode(String.valueOf(payOsResponse.getOrderCode()));
        donation.setPaymentStatus(String.valueOf(payOsResponse.getStatus()));

        donationRepository.save(donation);

        if (donationCreateRequest.isSendMail()) {
            this.sendMail(donationCreateRequest.getEmail(), donationCreateRequest.getAmount(), donation.getTransactionCode());
        }

        DonationCreateResponse donationCreateResponse = new DonationCreateResponse();
        donationCreateResponse.setQrCode(payOsResponse.getQrCode());
        donationCreateResponse.setCheckoutUrl(payOsResponse.getCheckoutUrl());
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

    public void cancelDonation(String transactionCode) {
        Optional<Donation> donationOptional = donationRepository.findByTransactionCode(transactionCode);
        if (donationOptional.isEmpty()) throw new ResourceNotFoundException("Donation", "code", transactionCode);
        Donation donation = donationOptional.get();

        donation.setPaymentStatus(PaymentLinkStatus.CANCELLED.getValue());
        payOsService.getPayOS().paymentRequests().cancel(Long.valueOf(transactionCode));
    }

    public void finishDonation(String transactionCode) {
        Optional<Donation> donationOptional = donationRepository.findByTransactionCode(transactionCode);
        if (donationOptional.isEmpty()) throw new ResourceNotFoundException("Donation", "code", transactionCode);
        Donation donation = donationOptional.get();

        donation.setPaymentStatus(PaymentLinkStatus.PAID.getValue());
    }

    public void sendMail(String email, Long amount, String transactionCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(hostMail);
        message.setTo(email);
        message.setSubject("Quỹ ủng hộ tài năng trẻ");
        message.setText("Xác nhận thông tin: \n" +
                "- Số tiền chuyển: " + amount + "\n" +
                "- Mã thanh toán: " + transactionCode);


        mailSender.send(message);

    }



}
