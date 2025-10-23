package com.graduation.youthtalentfund.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {

    @NotEmpty(message = "Họ tên không được để trống.")
    private String fullName;

    @NotEmpty(message = "Email không được để trống.")
    @Email(message = "Định dạng email không hợp lệ.")
    private String email;

    @NotEmpty(message = "Mật khẩu không được để trống.")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự.")
    private String password;
}
