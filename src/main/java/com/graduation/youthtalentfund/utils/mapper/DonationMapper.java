package com.graduation.youthtalentfund.utils.mapper;

import com.graduation.youthtalentfund.dtos.request.donate.DonationSearchRequest;
import com.graduation.youthtalentfund.dtos.request.donate.UserDonationStatisticRequest;
import com.graduation.youthtalentfund.dtos.response.donate.DonationDataResponse;
import com.graduation.youthtalentfund.entities.Campaign;
import com.graduation.youthtalentfund.entities.Donation;
import com.graduation.youthtalentfund.entities.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * This class map Donation entity data to DTO data
 */
public class DonationMapper {
    public static DonationDataResponse toResponseData(Donation donation) {
        DonationDataResponse dataResponse = new DonationDataResponse();
        dataResponse.setCode(donation.getCode());
        dataResponse.setAmount(donation.getAmount().longValueExact());
        dataResponse.setDonorName(donation.getDonorName());
        dataResponse.setDonorEmail(donation.getDonorEmail());
        dataResponse.setDonorPhoneNumber(donation.getDonorPhoneNumber());
        dataResponse.setMessage(donation.getMessage());
        dataResponse.setAnonymous(donation.isAnonymous());
        dataResponse.setTransactionCode(donation.getTransactionCode());
        dataResponse.setPaymentStatus(donation.getPaymentStatus());
        dataResponse.setCampaignCode(donation.getCampaign().getCode());
        if (donation.getUser() == null) {
            dataResponse.setUserCode(null);
        } else dataResponse.setUserCode(donation.getUser().getCode());
        dataResponse.setCreatedAt(donation.getCreatedAt());
        dataResponse.setUpdatedAt(donation.getUpdatedAt());

        return dataResponse;
    }

    public static Specification<Donation> toSpecification(DonationSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Donation, User> userJoin = root.join("user", JoinType.LEFT);

            if (request.getUserType() != null) {
                switch (request.getUserType()) {
                    case ONLY_USER:
                        predicates.add(cb.isNotNull(root.get("user")));
                        break;
                    case ONLY_GUEST:
                        predicates.add(cb.isNull(root.get("user")));
                        break;
                    case ALL:
                        // không add gì
                        break;
                }
            }

            // Filter theo userCode (chỉ áp dụng cho donation có user)
            if (request.getUserCode() != null && !request.getUserCode().isBlank()) {
                predicates.add(cb.like(userJoin.get("code"), "%" + request.getUserCode() + "%"));
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

    public static Specification<Donation> toSpecification(UserDonationStatisticRequest request) {
        return (root, query, cb) -> {

            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            // JOIN user
            Join<Donation, User> userJoin = root.join("user", JoinType.INNER);

            // JOIN campaign
            Join<Donation, Campaign> campaignJoin =
                    root.join("campaign", JoinType.INNER);

            // Chỉ donation thành công
            predicates.add(
                    cb.equal(root.get("paymentStatus"), "PAID")
            );

            // Không lấy null user
            predicates.add(
                    cb.isNotNull(root.get("user"))
            );

            // Filter userCode
            if (request.getUserCode() != null && !request.getUserCode().isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(userJoin.get("code")),
                                "%" + request.getUserCode().toLowerCase() + "%"
                        )
                );
            }

            // Filter campaign
            if (request.getCampaignCode() != null && !request.getCampaignCode().isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(campaignJoin.get("code")),
                                "%" + request.getCampaignCode() + "%")
                );
            }

            // Filter fromDate
            if (request.getFromDate() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("createdAt"),
                                request.getFromDate()
                        )
                );
            }

            // Filter toDate
            if (request.getToDate() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("createdAt"),
                                request.getToDate()
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
