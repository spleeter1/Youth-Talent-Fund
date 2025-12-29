package com.graduation.youthtalentfund.websocket;

import java.security.Principal;

public class DonationPrincipal implements Principal {

    private final String donationCode;

    public DonationPrincipal(String donationCode) {
        this.donationCode = donationCode;
    }

    @Override
    public String getName() {
        return donationCode;
    }
}