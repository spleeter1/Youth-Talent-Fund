package com.graduation.youthtalentfund.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvatarPathsDTO {
    private String original;
    private String thumbnail;
}
