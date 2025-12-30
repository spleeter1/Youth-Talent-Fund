package com.graduation.youthtalentfund.dtos.request.donate;

import com.graduation.youthtalentfund.enums.UserDonationSearchType;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DonationSearchRequest {
    private String userCode;
    private UserDonationSearchType userType;
    private String donationCode;
    private String campaignCode;
    private String donorName;
    private String donorEmail;
    private String donorPhoneNumber;
    private String message;
    private String paymentStatus;
    private Boolean isAnonymous;
    @PositiveOrZero
    private Integer pageNumber;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
