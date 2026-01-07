package com.graduation.youthtalentfund.ai.intent;

public enum IntentType {
    CHAT_GENERAL,

    // Campaign
    LIST_CAMPAIGNS,
    GET_CAMPAIGN_DETAIL,
    GET_LATEST_CAMPAIGN,
//    GET_CAMPAIGN_STATISTIC,
    GET_CAMPAIGN_DONATION_SUMMARY,

    // Donation
    GET_TOTAL_DONATION,

    // Proof / Transparency
//    GET_CAMPAIGN_PROOF_REPORTS,

    // User help
    HOW_TO_DONATE,
    UNKNOWN
}