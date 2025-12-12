package com.graduation.youthtalentfund.services.impl;

import com.graduation.youthtalentfund.dtos.request.donate.DonationCreateRequest;
import com.graduation.youthtalentfund.dtos.request.donate.DonationSearchRequest;
import com.graduation.youthtalentfund.dtos.response.donate.DonationCreateResponse;
import com.graduation.youthtalentfund.dtos.response.donate.DonationDataResponse;
import com.graduation.youthtalentfund.dtos.response.donate.DonationRptDTO;
import com.graduation.youthtalentfund.entities.Campaign;
import com.graduation.youthtalentfund.entities.CustomUserDetails;
import com.graduation.youthtalentfund.dtos.request.CreateDonationRptDTO;
import com.graduation.youthtalentfund.entities.Donation;
import com.graduation.youthtalentfund.entities.User;
import com.graduation.youthtalentfund.enums.ProofReportType;
import com.graduation.youthtalentfund.exceptions.ResourceNotFoundException;
import com.graduation.youthtalentfund.repositories.CampaignRepository;
import com.graduation.youthtalentfund.repositories.DonationRepository;
import com.graduation.youthtalentfund.repositories.UserRepository;
import com.graduation.youthtalentfund.services.DonationService;
import com.graduation.youthtalentfund.services.MailService;
import com.graduation.youthtalentfund.services.PayOsService;
import com.graduation.youthtalentfund.utils.AuthUtil;
import com.graduation.youthtalentfund.utils.CodeGenerator;
import com.graduation.youthtalentfund.utils.mapper.DonationMapper;
import jakarta.validation.ConstraintViolation;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;


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
    @Transactional
    public DonationRptDTO createDonationRpt(CreateDonationRptDTO createDonationRptDTO, Campaign campaign, ProofReportType proofReportType) {
        if (proofReportType != ProofReportType.EXPENSE && proofReportType != ProofReportType.CONTRIBUTION) return null;

        if(createDonationRptDTO == null)
            throw new IllegalArgumentException("Thông tin donation là bắt buộc với type " + proofReportType);

        BigDecimal amount = createDonationRptDTO.getTransaction();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Số tiền giao dịch của phải khác 0");
        }

        boolean isAnonymous = false;
        String donorName = "Người quyên góp không biết tên";
        String donorEmail = "";
        String donorPhone = "";
        String paymentStatus = null;
        if (proofReportType == ProofReportType.EXPENSE) {
            donorName = "Youth Talent Fund";
            donorEmail = "admin@youthtalentfund.com";
            donorPhone = "0340020112";
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                amount = amount.negate();
            }
            paymentStatus = "EXPENSE_MANUAL";
        } else {
            Set<ConstraintViolation<CreateDonationRptDTO>> violations =
                    validator.validate(createDonationRptDTO);

            if (!violations.isEmpty()) {
                String errors = violations.stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.joining(", "));
                throw new IllegalArgumentException("Thông tin donation không hợp lệ: " + errors);
            }

            isAnonymous = createDonationRptDTO.isAnonymous();
            donorName = createDonationRptDTO.getDonorName();
            donorEmail = createDonationRptDTO.getDonorEmail();
            donorPhone = createDonationRptDTO.getPhoneNumber();
            paymentStatus = "DONATE_MANUAL";
        }

        Donation donation = Donation.builder()
                .donorName(donorName)
                .donorEmail(donorEmail)
                .donorPhoneNumber(donorPhone)
                .isAnonymous(isAnonymous)
                .message(createDonationRptDTO.getMessage())
                .code(CodeGenerator.generateDonationCode())
                .amount(amount)
                .campaign(campaign)
                .paymentStatus(paymentStatus)
                .build();
        BigDecimal currentAmount = campaign.getCurrentAmount();
        campaign.setCurrentAmount(amount.compareTo(BigDecimal.ZERO) > 0 ? currentAmount.add(amount) : currentAmount);
        campaignRepository.save(campaign);
        Donation saved = donationRepository.save(donation);

        return DonationRptDTO.builder()
                .code(saved.getCode())
                .amount(saved.getAmount())
                .donorName(saved.getDonorName())
                .donorEmail(saved.getDonorEmail())
                .donorPhoneNumber(saved.getDonorPhoneNumber())
                .message(saved.getMessage())
                .isAnonymous(saved.isAnonymous())
                .transactionCode(saved.getTransactionCode())
                .paymentStatus(saved.getPaymentStatus())
                .build();
    }
}
