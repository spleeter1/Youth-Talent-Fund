package com.graduation.youthtalentfund.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ForgotPasswordDTO {
    @NotEmpty(message = "Email không được để trống.")
    @Email(message = "Định dạng email không hợp lệ.")
    private String email;
}
