package com.graduation.youthtalentfund.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentWebSocketStatusMessage {
    private String status;
    private String orderCode;
}
