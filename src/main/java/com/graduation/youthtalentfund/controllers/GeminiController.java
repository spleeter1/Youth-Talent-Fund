package com.graduation.youthtalentfund.controllers;

import com.graduation.youthtalentfund.services.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GeminiController {
    @Value("${google.gemini.key}")
    private String apiKey;

    private final GeminiService geminiService;

    @PostMapping("/public/chat")
    public ResponseEntity<String> chat(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(geminiService.chat(request.get("prompt")));
    }
}
