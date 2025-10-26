package com.graduation.youthtalentfund.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserInfoDTO {
    private String code;
    private String fullName;
    private String email;
    private String avatarPath;
    private String address;
    private String bio;
    private List<String> roles;
}
