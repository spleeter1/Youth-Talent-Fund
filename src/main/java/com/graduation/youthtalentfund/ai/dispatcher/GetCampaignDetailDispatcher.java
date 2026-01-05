package com.graduation.youthtalentfund.ai.dispatcher;

import com.graduation.youthtalentfund.ai.intent.IntentType;
import com.graduation.youthtalentfund.ai.model.QueryContext;
import com.graduation.youthtalentfund.ai.model.QueryResult;
import com.graduation.youthtalentfund.services.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetCampaignDetailDispatcher implements DataQueryDispatcher{
    private final CampaignService campaignService;

    @Override
    public boolean supports(QueryContext context) {
        return context.getIntent() == IntentType.GET_CAMPAIGN_DETAIL;
    }

    @Override
    public QueryResult dispatch(QueryContext context) {

        Object rawName = context.getParams().get("campaignName");

        if (rawName == null) {
            return new QueryResult(
                    context.getIntent(),
                    "Bạn muốn hỏi về chiến dịch nào?"
            );
        }

        String name = rawName.toString();

        var page = campaignService.searchCampaigns(
                null, null, name, 0, 1
        );

        if (page.isEmpty()) {
            return new QueryResult(
                    context.getIntent(),
                    "Không tìm thấy chiến dịch phù hợp"
            );
        }

        return new QueryResult(
                context.getIntent(),
                page.getContent().get(0)
        );
    }
}
