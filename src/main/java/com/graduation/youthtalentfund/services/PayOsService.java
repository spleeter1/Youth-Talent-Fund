package com.graduation.youthtalentfund.services;

import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;

public interface PayOsService {
    /**
     * Gửi request tạo PaymentLink tới PayOS
     * @param request Request schema
     * @return response của PayOS (signature đã xác thực)
     */
    CreatePaymentLinkResponse createPaymentLink(CreatePaymentLinkRequest request);

    /**
     * Tạo Signature cho {@link CreatePaymentLinkRequest}
     * @param jsonData Data dùng để tạo signature
     * @return Signature string
     */
    String createPaymentRequestSignature(Object jsonData);

    PayOS getPayOS();
}
