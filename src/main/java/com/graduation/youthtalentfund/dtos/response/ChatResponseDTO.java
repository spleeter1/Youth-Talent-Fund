package com.graduation.youthtalentfund.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatResponseDTO {
    private String reply;
    private String conversationId;
}
