package com.graduation.youthtalentfund.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUrlResponseDTO {
    private String original;
    private String thumbnail;
}