package com.graduation.youthtalentfund.ai.model;

import com.graduation.youthtalentfund.ai.intent.IntentType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QueryResult {
    private IntentType intent;
    private Object data;
}
