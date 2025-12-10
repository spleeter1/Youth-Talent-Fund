package com.graduation.youthtalentfund.dtos.request;

import com.graduation.youthtalentfund.enums.ProofReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

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

}
