package com.graduation.youthtalentfund.utils.mapper;

import com.graduation.youthtalentfund.dtos.response.donate.DonationDataResponse;
import com.graduation.youthtalentfund.entities.Donation;

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
        dataResponse.setUserCode(donation.getUser().getCode());
        dataResponse.setCreatedAt(donation.getCreatedAt());
        dataResponse.setUpdatedAt(donation.getUpdatedAt());

        return dataResponse;
    }
}
