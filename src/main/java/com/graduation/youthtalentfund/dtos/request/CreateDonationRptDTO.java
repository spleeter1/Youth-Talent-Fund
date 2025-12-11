package com.graduation.youthtalentfund.dtos.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CreateDonationRptDTO {
    @Size(max = 100, message = "Tên người quyên góp không được vượt quá 100 ký tự")
    private String donorName;

    @Email(message = "Email không đúng định dạng")
    @Size(max = 150, message = "Email không được vượt quá 150 ký tự")
    private String donorEmail;

    @Pattern(
            regexp = "^(\\+?84|0)(\\d{9})$",
            message = "Số điện thoại không hợp lệ"
    )
    private String phoneNumber;

    @Size(max = 255, message = "Lời nhắn không được vượt quá 255 ký tự")
    private String message;

    @NotNull(message = "Số tiền giao dịch không được để trống")
    private BigDecimal transaction;

    private boolean isAnonymous;
}
