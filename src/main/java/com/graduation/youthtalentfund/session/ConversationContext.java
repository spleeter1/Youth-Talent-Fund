package com.graduation.youthtalentfund.session;

import com.google.genai.types.Content;
import com.google.genai.types.Part;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class ConversationContext {
    private final List<Content> history = new ArrayList<>();
    private long lastAccessTime;

    public void add(Content content) {
        history.add(content);
        touch();
    }

    public ConversationContext() {
        this.touch();
    }

    public void addUserMessage(String text) {
        Content content = Content.builder()
                .role("user")
                .parts(Collections.singletonList(Part.fromText(text)))
                .build();
        this.history.add(content);
        touch();
    }

    public void addModelMessage(String text) {
        Content content = Content.builder()
                .role("model")
                .parts(Collections.singletonList(Part.fromText(text)))
                .build();
        this.history.add(content);
        touch();
    }

    public boolean isExpired(long ttlMs) {
        return System.currentTimeMillis() - lastAccessTime > ttlMs;
    }

    private void touch() {
        lastAccessTime = System.currentTimeMillis();
    }
}
