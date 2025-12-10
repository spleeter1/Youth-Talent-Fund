package com.graduation.youthtalentfund.utils;

import com.graduation.youthtalentfund.dtos.response.FileUrlResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class FileUtils {
    private static String CDN_BASE_URL;
    private static String BUCKET_NAME;

    @Value("${cdn.base-url}")
    public void setCdnBaseUrl(String cdnBaseUrl) {
        FileUtils.CDN_BASE_URL = cdnBaseUrl;
    }

    @Value("${minio.bucket-name}")
    public void setBucketName(String bucketName) {
        FileUtils.BUCKET_NAME = bucketName;
    }

    public static FileUrlResponseDTO build(String objectPath) {
        if (!StringUtils.hasText(objectPath)) {
            return null;
        }

        String originalUrl = buildUrl(objectPath);
        String thumbnailUrl = buildUrl("thumb_" + objectPath);

        return FileUrlResponseDTO.builder()
                .original(originalUrl)
                .thumbnail(thumbnailUrl)
                .build();
    }

    public static FileUrlResponseDTO buildFile(String objectPath) {
        if (!StringUtils.hasText(objectPath)) {
            return null;
        }

        return FileUrlResponseDTO.builder()
                .original(buildUrl(objectPath))
                .thumbnail(null)
                .build();
    }

    private static String buildUrl(String objectPath) {
        return String.format("%s/%s/%s", CDN_BASE_URL, BUCKET_NAME, objectPath);
    }
}