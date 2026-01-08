package com.graduation.youthtalentfund.dtos.request.donate;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Data
public class DonationCreateRequest {

    @NotEmpty(message = "Họ tên không được để trống.")
    private String name;

    @NotEmpty(message = "Email không được để trống.")
    @Email(message = "Định dạng email không hợp lệ.")
    private String email;

    @NotEmpty(message = "Số điện thoại không được để trống.")
    private String phoneNumber;

    @NotNull(message = "Không được để trống")
    @Min(value = 2000, message = "Ít nhất 2000")
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

    @JsonProperty("isAnonymous")
    private Boolean isAnonymous;
    private Boolean sendMail;

}
