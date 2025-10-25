package com.graduation.youthtalentfund.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserInfoDTO {
    private String fullName;
    private String email;
    private String avatarPath;
    private List<String> roles;
}
