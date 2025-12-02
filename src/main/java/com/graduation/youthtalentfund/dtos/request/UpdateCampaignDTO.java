package com.graduation.youthtalentfund.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.graduation.youthtalentfund.enums.CampaignCategory;
import com.graduation.youthtalentfund.enums.CampaignStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UpdateCampaignDTO {
    @NotBlank(message = "Tên chiến dịch không được để trống")
    @Size(min = 10, max = 200, message = "Tên chiến dịch phải từ 10 đến 200 ký tự")
    private String title;

    @NotBlank(message = "Mô tả ngắn không được để trống")
    private String description;

    @NotBlank(message = "Địa điểm không được để trống")
    private String location;
    private String story;

    @NotNull(message = "Mục tiêu tài chính bắt buộc phải có")
    @DecimalMin(value = "1000000.0", message = "Mục tiêu gây quỹ tối thiểu là 1.000.000 VNĐ")
    private BigDecimal targetAmount;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu phải là hiện tại hoặc tương lai")
    private LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @Future(message = "Ngày kết thúc phải là tương lai")
    private LocalDateTime endDate;

    @NotNull(message = "Category is required")
    private CampaignCategory category;

    private String assigneeCode;

    @AssertTrue(message = "Ngày kết thúc phải diễn ra sau ngày bắt đầu ít nhất 7 ngày")
    @JsonIgnore
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return endDate.isAfter(startDate.plusDays(7));
    }
}
