package com.graduation.youthtalentfund.ai.intent;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.graduation.youthtalentfund.ai.model.QueryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResponseComposerService {
    private final Client client;

    @Value("${frontend.url}")
    private String frontendBaseUrl;

    public String compose(QueryResult result) {
        String prompt = """
                Bạn là trợ lí AI, nhiệm vụ là diễn đạt dữ liệu cho người dùng
                KHÔNG được suy đoán thêm.
                KHÔNG được thay đổi số liệu
                Nhưng cũng trả lời linh hoạt về mặt diễn đạt tí nhé,đừng quá máy móc theo dữ liệu nhưnng vẫn phải đúng format phản hồi" 
                Domain gốc nếu cần thiết khi muốn đưa đường dẫn cho người dùng này: %s
                Trình bày đẹp khi cần xuống dòng
                Có thể đọc thông tin và ghép vs link chuẩn để redirect cho dễ nếu cần thiết. 
                
                Intent: %s
                Dữ liệu:
                %s
                
                Hãy trả lời bằng tiếng Việt, ngắn gọn, rõ ràng. Format là: 
                Intent: 
                "Câu trả lời"
                """.formatted(frontendBaseUrl,
                result.getIntent(),
                result.getData().toString()
        );

        GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", prompt, null);
        System.out.println("result khi gọi lần 1: " + result.getIntent());
        return response.text();
    }
}
