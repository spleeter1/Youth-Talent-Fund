package com.graduation.youthtalentfund.dtos.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileDTO {
    @Size(max = 50, message = "Họ tên không được vượt quá 50 ký tự.")
    private String fullName;

    @Pattern(
            regexp = "^(?:\\+84|0)[3|5|7|8|9][0-9]{8}$",
            message = "Số điện thoại không hợp lệ. Vui lòng nhập dạng 0xxxxxxxxx hoặc +84xxxxxxxxx"
    )
    private String phoneNumber;

    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự.")
    private String address;

    @Size(max = 500, message = "Tiểu sử không được vượt quá 500 ký tự.")
    private String bio;
}
