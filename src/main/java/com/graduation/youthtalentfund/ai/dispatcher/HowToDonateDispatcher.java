package com.graduation.youthtalentfund.ai.dispatcher;

import com.graduation.youthtalentfund.ai.intent.IntentType;
import com.graduation.youthtalentfund.ai.model.QueryContext;
import com.graduation.youthtalentfund.ai.model.QueryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HowToDonateDispatcher implements DataQueryDispatcher {

    @Value("${frontend.url}")
    private String frontendBaseUrl;

    @Override
    public boolean supports(QueryContext context) {
        return context.getIntent() == IntentType.HOW_TO_DONATE;
    }

    @Override
    public QueryResult dispatch(QueryContext context) {

        String donateUrl = frontendBaseUrl
                + "/campaign/" + "{campaignCode}"
                + "?mode=donation";

        return new QueryResult(
                context.getIntent(),
                """
                Các bước quyên góp:
                1. Chọn chiến dịch
                2. Điền thông tin người ủng hộ
                3. Quét mã QR
                4. Hoàn tất quyên góp

                👉 Truy cập trực tiếp để quyên góp:
                %s
                """.formatted(donateUrl)
        );
    }
}

