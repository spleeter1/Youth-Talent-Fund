package com.graduation.youthtalentfund.controllers;

import com.graduation.youthtalentfund.services.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GeminiController {
    @Value("${google.gemini.key}")
    private String apiKey;

    private final GeminiService geminiService;
}
