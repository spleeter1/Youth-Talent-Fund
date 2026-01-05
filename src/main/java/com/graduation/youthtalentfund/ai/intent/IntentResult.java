package com.graduation.youthtalentfund.ai.intent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Map;

@Getter
@AllArgsConstructor
public class IntentResult {
    private IntentType intent;
    private Map<String, Object> params;
}
