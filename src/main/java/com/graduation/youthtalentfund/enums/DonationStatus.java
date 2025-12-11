package com.graduation.youthtalentfund.enums;

// Hiện tại đang dùng PaymentLinkStatus của PayOS thay enum này
public enum DonationStatus {
    PENDING,
    CANCELLED,
    UNDERPAID,
    PAID,
    EXPIRED,
    PROCESSING,
    FAILED;
}
