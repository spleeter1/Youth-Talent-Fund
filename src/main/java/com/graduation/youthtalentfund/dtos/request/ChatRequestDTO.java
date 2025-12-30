package com.graduation.youthtalentfund.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequestDTO {
    private String conversationId;
    @NotBlank
    private String message;
}
