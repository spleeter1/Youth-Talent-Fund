package com.graduation.youthtalentfund.services.impl;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.graduation.youthtalentfund.ai.dispatcher.DataQueryDispatcherRegistry;
import com.graduation.youthtalentfund.ai.intent.GeminiIntentAnalyzer;
import com.graduation.youthtalentfund.ai.intent.IntentResult;
import com.graduation.youthtalentfund.ai.intent.IntentType;
import com.graduation.youthtalentfund.ai.intent.ResponseComposerService;
import com.graduation.youthtalentfund.ai.model.QueryContext;
import com.graduation.youthtalentfund.ai.model.QueryResult;
import com.graduation.youthtalentfund.dtos.request.ChatRequestDTO;
import com.graduation.youthtalentfund.dtos.response.ChatResponseDTO;
import com.graduation.youthtalentfund.services.GeminiService;
import com.graduation.youthtalentfund.session.ConversationContext;
import com.graduation.youthtalentfund.session.ConversationStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class GeminiServiceImpl implements GeminiService {
    private final Client client;
    private final ConversationStore conversationStore;
    private final GeminiIntentAnalyzer intentAnalyzer;
    private final DataQueryDispatcherRegistry dispatcherRegistry;
    private final ResponseComposerService responseComposer;

    @Override
    public ChatResponseDTO chat(ChatRequestDTO request) {
        String conversationId = request.getConversationId();
        if (conversationId == null || conversationId.isBlank()) {
            conversationId = conversationStore.createConversation();
        }

        ConversationContext context = conversationStore.getOrCreate(conversationId);

        context.addUserMessage(request.getMessage());

        IntentResult intentResult = intentAnalyzer.analyze(request.getMessage());

        QueryResult queryResult =
                dispatcherRegistry.dispatch(
                        new QueryContext(
                                conversationId,
                                request.getMessage(),
                                intentResult.getIntent(),
                                intentResult.getParams()
                        )
                );

        String answer = responseComposer.compose(queryResult);

        context.addModelMessage(answer);

        return ChatResponseDTO.builder()
                .reply(answer)
                .conversationId(conversationId)
                .build();
    }
}
