package com.graduation.youthtalentfund.dtos.request.donate;

import com.graduation.youthtalentfund.entities.Donation;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class DonationSearchRequest {
    private String userCode;
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

    public static Specification<Donation> buildSpecification(DonationSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getUserCode() != null) {
                predicates.add(cb.like(root.get("user").get("code"), "%" + request.getUserCode() + "%"));
            }

            if (request.getDonationCode() != null) {
                predicates.add(cb.like(root.get("code"), "%" + request.getDonationCode() + "%"));
            }

            if (request.getCampaignCode() != null && !request.getCampaignCode().isEmpty()) {
                predicates.add(cb.like(root.get("campaign").get("code"), "%" + request.getCampaignCode() + "%"));
            }

            if (request.getDonorName() != null && !request.getDonorName().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("donorName")),"%" + request.getDonorName().toLowerCase() + "%"));
            }

            if (request.getPaymentStatus() != null && !request.getPaymentStatus().isEmpty()) {
                predicates.add(cb.equal(root.get("paymentStatus"), request.getPaymentStatus()));
            }

            if (request.getMinAmount() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), request.getMinAmount()));
            }

            if (request.getMaxAmount() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("amount"), request.getMaxAmount()));
            }

            if (request.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), request.getStartDate()));
            }

            if (request.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), request.getEndDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
