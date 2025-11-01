package com.graduation.youthtalentfund.services.impl;

import com.graduation.youthtalentfund.config.PayOsConfig;
import com.graduation.youthtalentfund.services.PayOsService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;

@Service
@RequiredArgsConstructor
public class PayOsServiceImpl implements PayOsService {

    private final PayOsConfig config;

    @Getter
    private PayOS payOS;

    @PostConstruct
    void init() {
        this.payOS = new PayOS(config.getClientId(), config.getApiKey(), config.getChecksumKey());
    }

    @Override
    public CreatePaymentLinkResponse createPaymentLink(CreatePaymentLinkRequest request) {
        return payOS.paymentRequests().create(request);
    }

    @Override
    public String createPaymentRequestSignature(Object jsonData) {
        return payOS.getCrypto().createSignatureOfPaymentRequest(jsonData, config.getChecksumKey());
    }


}
