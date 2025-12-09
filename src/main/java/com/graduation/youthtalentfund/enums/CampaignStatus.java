package com.graduation.youthtalentfund.enums;

public enum CampaignStatus {
    PENDING,
    IN_PROGRESS,
    ON_HOLD,
    COMPLETED,
    CANCELLED;

    public boolean canTransitionTo(CampaignStatus target) {
        return switch(this) {
            case PENDING -> target == IN_PROGRESS || target == CANCELLED;
            case IN_PROGRESS -> target == ON_HOLD || target == COMPLETED;
            case ON_HOLD -> target == IN_PROGRESS || target == COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}