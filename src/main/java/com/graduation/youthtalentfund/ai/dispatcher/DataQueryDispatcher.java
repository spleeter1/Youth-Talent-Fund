package com.graduation.youthtalentfund.ai.dispatcher;

import com.graduation.youthtalentfund.ai.model.QueryContext;
import com.graduation.youthtalentfund.ai.model.QueryResult;

public interface DataQueryDispatcher {
    boolean supports(QueryContext context); //dispatch nào xử lý intent;
    QueryResult dispatch(QueryContext context); //chạy nghiệp vụ thật
}
