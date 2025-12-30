package com.graduation.youthtalentfund.services.impl;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
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

    @Override
    public ChatResponseDTO chat(ChatRequestDTO request) {
        String conversationId = request.getConversationId();
        if (conversationId == null || conversationId.isBlank()) {
            conversationId = conversationStore.createConversation();
        }

        ConversationContext context = conversationStore.getOrCreate(conversationId);

        context.addUserMessage(request.getMessage());

        try {
            GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", context.getHistory(), null);

            String answer = response.text();

            context.addModelMessage(answer);

            return ChatResponseDTO.builder()
                    .reply(answer)
                    .conversationId(conversationId)
                    .build();
        } catch (Exception e) {
            return ChatResponseDTO.builder()
                    .conversationId(conversationId)
                    .reply("Phản lồi bị lỗi ")
                    .build();
        }
    }
}
