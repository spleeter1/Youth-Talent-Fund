package com.graduation.youthtalentfund.dtos.response.donate;

import lombok.Data;

@Data
public class DonationCreateResponse {
    private String qrCode;
    private String checkoutUrl;
}
