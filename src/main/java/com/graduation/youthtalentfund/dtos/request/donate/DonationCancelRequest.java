package com.graduation.youthtalentfund.dtos.request.donate;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DonationCancelRequest {
    @Positive
    @NotEmpty
    private final Long transactionCode;
}
