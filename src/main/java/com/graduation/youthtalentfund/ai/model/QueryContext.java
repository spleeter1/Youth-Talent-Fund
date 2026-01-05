package com.graduation.youthtalentfund.ai.model;

import com.graduation.youthtalentfund.ai.intent.IntentType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class QueryContext {
    private String conversationId;
    private String userMessage;
    private IntentType intent;
    private Map<String, Object> params;
}
