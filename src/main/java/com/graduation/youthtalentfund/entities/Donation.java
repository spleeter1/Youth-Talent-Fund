package com.graduation.youthtalentfund.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "donations", indexes = {
        @Index(name = "idx_donation_code", columnList = "code", unique = true),
        @Index(name = "idx_donation_transaction_code", columnList = "transactionCode", unique = true)
})
public class Donation extends BaseEntity {

    @Column(length = 50, nullable = false)
    private String code;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String donorName;

    @Column
    private String donorEmail;

    @Column
    private String donorPhoneNumber;

    @Lob
    private String message;

    @Column(nullable = false)
    private boolean isAnonymous = false;

    @Column(length = 50)
    private String transactionCode;

    @Column(length = 50, nullable = false)
    private String paymentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
}
