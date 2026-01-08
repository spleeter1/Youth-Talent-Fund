package com.graduation.youthtalentfund.dtos.response.donate;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DonationCreateResponse {
    @NotEmpty
    private String qrCode;
    @NotEmpty
    private String checkoutUrl;
    private String accountNumber;
    private String accountName;
    private String description;
    private String wsToken;
}
