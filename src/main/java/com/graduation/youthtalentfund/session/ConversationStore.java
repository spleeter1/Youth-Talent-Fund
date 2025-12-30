package com.graduation.youthtalentfund.session;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConversationStore {

    private static final long TTL_MS = 30 * 60 * 1000;

    private final Map<String, ConversationContext> store =
            new ConcurrentHashMap<>();

    public String createConversation() {
        String id = UUID.randomUUID().toString();
        store.put(id, new ConversationContext());
        return id;
    }

    public ConversationContext getOrCreate(String conversationId) {
        return store.computeIfAbsent(
                conversationId,
                id -> new ConversationContext()
        );
    }

    @Scheduled(fixedRate = 60_000)
    public void cleanup() {
        store.entrySet().removeIf(e -> e.getValue().isExpired(TTL_MS)
        );
    }
}
