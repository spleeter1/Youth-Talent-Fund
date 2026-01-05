package com.graduation.youthtalentfund.ai.dispatcher;

import com.graduation.youthtalentfund.ai.intent.IntentType;
import com.graduation.youthtalentfund.ai.model.QueryContext;
import com.graduation.youthtalentfund.ai.model.QueryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatGeneralDispatcher implements DataQueryDispatcher{
    @Override
    public boolean supports(QueryContext context) {
        return context.getIntent() == IntentType.CHAT_GENERAL
                || context.getIntent() == IntentType.UNKNOWN;
    }

    @Override
    public QueryResult dispatch(QueryContext context) {
        return new QueryResult(
                context.getIntent(),
                "Ví dụ trả lời: Linh hoạt câu nói sau nha \"Mình có thể giúp bạn về các chiến dịch và thông tin quyên góp\" Trả lời vậy nếu là cảm thấy nó là UNKNOWN còn không thì hiểu là GENERAL thì cứ đối thoại bình thường"
        );
    }
}
