package com.graduation.youthtalentfund.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResponseDTO {
    private String originalFilename;
    private String filePath;
    private String fileUrl;
    private String fileType;
    private long fileSize;
}
