package com.graduation.youthtalentfund.ai.dispatcher;

import com.graduation.youthtalentfund.ai.intent.IntentType;
import com.graduation.youthtalentfund.ai.model.QueryContext;
import com.graduation.youthtalentfund.ai.model.QueryResult;
import com.graduation.youthtalentfund.services.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetLatestCampaignDispatcher implements DataQueryDispatcher {

    private final CampaignService campaignService;

    @Override
    public boolean supports(QueryContext context) {
        return context.getIntent() == IntentType.GET_LATEST_CAMPAIGN || context.getIntent() == IntentType.LIST_CAMPAIGNS;
    }

    @Override
    public QueryResult dispatch(QueryContext context) {

        var page = campaignService.searchCampaigns(
                null, null, null, 0, 1
        );

        if (page.isEmpty()) {
            return new QueryResult(
                    context.getIntent(),
                    "Hiện tại chưa có chiến dịch nào"
            );
        }

        return new QueryResult(
                context.getIntent(),
                page.getContent()
        );
    }
}

