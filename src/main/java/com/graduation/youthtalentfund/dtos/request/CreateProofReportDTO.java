package com.graduation.youthtalentfund.dtos.request;

import com.graduation.youthtalentfund.enums.ProofReportType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProofReportDTO {
    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(min = 10, max = 50, message = "Tiêu đề phải từ 10 đến 50 ký tự")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    @Size(min = 10, max = 2000, message = "Nội dung phải từ 10 đến 2000 ký tự")
    private String content;

    @NotNull(message = "Loại minh chứng là bắt buộc ")
    private ProofReportType type;

    @Digits(integer = 15, fraction = 2, message = "Số tiền không hợp lệ")
    @DecimalMin(value = "1000.00", message = "Số tiền phải lớn hơn 1000")
    private BigDecimal transactionAmount;
}
