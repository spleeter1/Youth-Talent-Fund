package com.graduation.youthtalentfund.ai.dispatcher;

import com.graduation.youthtalentfund.ai.intent.IntentType;
import com.graduation.youthtalentfund.ai.model.QueryContext;
import com.graduation.youthtalentfund.ai.model.QueryResult;
import com.graduation.youthtalentfund.services.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class GetTotalDonationDispatcher implements DataQueryDispatcher {

    private final DonationService donationService;

    @Override
    public boolean supports(QueryContext context) {
        return context.getIntent() == IntentType.GET_TOTAL_DONATION;
    }

    @Override
    public QueryResult dispatch(QueryContext context) {
        return new QueryResult(context.getIntent(), donationService.getTotalDonationStatistic(LocalDateTime.of(2022, 01, 01, 00, 00, 00), LocalDateTime.now()));
    }
}

