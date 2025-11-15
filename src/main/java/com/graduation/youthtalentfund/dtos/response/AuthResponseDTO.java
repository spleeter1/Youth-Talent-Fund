package com.graduation.youthtalentfund.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    private String accessToken;
    @Builder.Default
    private String tokenType = "Bearer ";
    private UserInfoDTO userInfo;
}
