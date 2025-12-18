package com.graduation.youthtalentfund.services.impl;

import com.graduation.youthtalentfund.services.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiServiceImpl implements GeminiService {
    @Value("${google.gemini.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String chat(String prompt) {

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey;

        // đưa lại request về dạng json
        Map<String, Object> bodyRequest = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(bodyRequest, headers);
        // call gemini
        ResponseEntity<?> response = restTemplate.postForEntity(url, request, Map.class);
        // parse json response
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        if(body == null) return null;

        List<?> candidates = (List<?>) body.get("candidates");
        if (candidates == null || candidates.isEmpty()) return null;

        Map<?, ?> candidate = (Map<?, ?>) candidates.getFirst();
        Map<?, ?> content = (Map<?, ?>) candidate.get("content");

        Map<?, ?> part = (Map<?, ?>) ((List<?>) content.get("parts")).getFirst();

        return part.get("text").toString();
    }
}
