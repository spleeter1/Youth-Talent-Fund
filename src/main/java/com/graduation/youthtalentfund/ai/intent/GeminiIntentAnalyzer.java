package com.graduation.youthtalentfund.ai.intent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeminiIntentAnalyzer {
    private final Client client;
    private final ObjectMapper objectMapper;

    public IntentResult analyze(String userMessage) {
        String prompt = """
                Bạn là hệ thống phân tích intent của web quỹ từ thiện
                Chỉ trả về JSON, KHÔNG giải thích.
                Hạn chế việc unknown nếu k thực sự cần thiết
                
                Các intent hợp lệ:
                    CHAT_GENERAL,
                    LIST_CAMPAIGNS,
                    GET_CAMPAIGN_DETAIL,
                    GET_LATEST_CAMPAIGN,
                    GET_CAMPAIGN_DONATION_SUMMARY,
                    GET_TOTAL_DONATION,
                    HOW_TO_DONATE,
                    UNKNOWN
                Câu hỏi của người dùng:
                "%s"
                
                Trả về format:
                {
                    "intent": "..."
                    "params": { }
                }
                """.formatted(userMessage);

        GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", prompt, null);

        System.out.println(response.text());
        String raw = response.text();
        try {
            String json = extractJson(raw);
            Map<String, Object> parsed = objectMapper.readValue(json, Map.class);

            String intentStr = parsed.get("intent").toString().trim();
            IntentType intent = IntentType.valueOf(intentStr);

            return new IntentResult(intent, parsed);

        } catch (Exception e) {
            for (IntentType type : IntentType.values()) {
                if (raw.contains(type.name())) {
                    return new IntentResult(type, Map.of());
                }
            }

            return new IntentResult(IntentType.UNKNOWN, Map.of());
        }

    }
    private String extractJson(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Gemini response is null");
        }

        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');

        if (start == -1 || end == -1 || end < start) {
            throw new IllegalArgumentException(
                    "No JSON object found in Gemini response:\n" + raw
            );
        }

        return raw.substring(start, end + 1);
    }
}
