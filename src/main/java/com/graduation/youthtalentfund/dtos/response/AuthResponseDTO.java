package com.graduation.youthtalentfund.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    private String accessToken;
    private String tokenType = "Bearer ";

    // Constructor tiện lợi chỉ nhận accessToken
    public AuthResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }
}
