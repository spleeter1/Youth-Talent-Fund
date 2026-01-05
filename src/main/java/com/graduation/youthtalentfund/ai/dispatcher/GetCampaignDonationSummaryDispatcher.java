package com.graduation.youthtalentfund.ai.dispatcher;

import com.graduation.youthtalentfund.ai.intent.IntentType;
import com.graduation.youthtalentfund.ai.model.QueryContext;
import com.graduation.youthtalentfund.ai.model.QueryResult;
import com.graduation.youthtalentfund.services.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetCampaignDonationSummaryDispatcher implements DataQueryDispatcher {

    private final DonationService donationService;

    @Override
    public boolean supports(QueryContext context) {
        return context.getIntent() == IntentType.GET_CAMPAIGN_DONATION_SUMMARY;
    }

    @Override
    public QueryResult dispatch(QueryContext context) {

        Object rawCode = context.getParams().get("campaignCode");

        if (rawCode == null) {
            return new QueryResult(
                    context.getIntent(),
                    "Bạn đang hỏi thống kê của chiến dịch nào?"
            );
        }

        return new QueryResult(
                context.getIntent(),
                donationService.getDonationListPublicOfCampaign(
                        rawCode.toString(), 0
                )
        );
    }
}

