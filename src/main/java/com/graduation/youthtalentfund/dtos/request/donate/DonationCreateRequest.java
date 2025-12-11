package com.graduation.youthtalentfund.dtos.request.donate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class DonationCreateRequest {

    @NotEmpty(message = "Họ tên không được để trống.")
    private String name;

    @NotEmpty(message = "Email không được để trống.")
    @Email(message = "Định dạng email không hợp lệ.")
    private String email;

    @NotEmpty(message = "Số điện thoại không được để trống.")
    private String phoneNumber;

    @NotEmpty(message = "Số tiền không được để trống.")
    private Long amount;

    @NotEmpty(message = "returnUrl không được để trống.")
    @URL(message = "Url không đúng định dạng")
    private String returnUrl;

    @NotEmpty(message = "cancelUrl không được để trống.")
    @URL(message = "Url không đúng định dạng")
    private String cancelUrl;

    @NotEmpty(message = "campaignCode không được để trống.")
    private String campaignCode;

    private String message;

    private boolean isAnonymous;
    private boolean sendMail;

}
