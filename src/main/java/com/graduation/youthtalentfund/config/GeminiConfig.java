package com.graduation.youthtalentfund.config;

import com.google.genai.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiConfig {
    @Bean
    public Client geminiClient(
            @Value("${google.gemini.key}") String apiKey
    ) {
        return Client.builder().apiKey(apiKey).build();
    }
}
