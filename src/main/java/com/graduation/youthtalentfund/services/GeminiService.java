package com.graduation.youthtalentfund.services;

import com.graduation.youthtalentfund.dtos.request.ChatRequestDTO;
import com.graduation.youthtalentfund.dtos.response.ChatResponseDTO;

public interface GeminiService {
    ChatResponseDTO chat (ChatRequestDTO prompt);
}
