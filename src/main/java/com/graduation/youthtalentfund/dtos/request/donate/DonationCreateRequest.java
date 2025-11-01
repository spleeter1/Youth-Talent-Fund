package com.graduation.youthtalentfund.dtos.request.donate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class DonationCreateRequest {

    @NotEmpty(message = "Họ tên không được để trống.")
    private final String name;

    @NotEmpty(message = "Email không được để trống.")
    @Email(message = "Định dạng email không hợp lệ.")
    private final String email;

    @NotEmpty(message = "Số điện thoại không được để trống.")
    private final String phoneNumber;

    @NotEmpty(message = "Số tiền không được để trống.")
    private final Long amount;

    @NotEmpty(message = "returnUrl không được để trống.")
    @URL(message = "Url không đúng định dạng")
    private final String returnUrl;

    @NotEmpty(message = "cancelUrl không được để trống.")
    @URL(message = "Url không đúng định dạng")
    private final String cancelUrl;

    @NotEmpty(message = "campaignCode không được để trống.")
    private final String campaignCode;

    private String message;

    private boolean isAnonymous;
    private boolean sendMail;

    private String userCode;

}
