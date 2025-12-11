package com.graduation.youthtalentfund.dtos.response.donate;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DonationCreateResponse {
    @NotEmpty
    private String qrCode;
    @NotEmpty
    private String checkoutUrl;
    @NotEmpty
    private String signature;
}
