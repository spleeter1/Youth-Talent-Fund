package com.graduation.youthtalentfund.ai.dispatcher;

import com.graduation.youthtalentfund.ai.model.QueryContext;
import com.graduation.youthtalentfund.ai.model.QueryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataQueryDispatcherRegistry {
    private final List<DataQueryDispatcher> dispatchers;

    public QueryResult dispatch(QueryContext context) {

        return dispatchers.stream()
                .filter(d -> d.supports(context))
                .findFirst()
                .map(d -> d.dispatch(context))
                .orElseThrow(() -> new IllegalStateException("No dispatcher for intent: " + context.getIntent()));
    }
}
