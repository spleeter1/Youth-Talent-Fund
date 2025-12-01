package com.graduation.youthtalentfund.utils;

import com.graduation.youthtalentfund.dtos.response.ImageResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ImageUtils {
    private static String CDN_BASE_URL;
    private static String BUCKET_NAME;

    @Value("${cdn.base-url}")
    public void setCdnBaseUrl(String cdnBaseUrl) {
        ImageUtils.CDN_BASE_URL = cdnBaseUrl;
    }

    @Value("${minio.bucket-name}")
    public void setBucketName(String bucketName) {
        ImageUtils.BUCKET_NAME = bucketName;
    }

    public static ImageResponseDTO build(String objectPath) {
        if (!StringUtils.hasText(objectPath)) {
            return null;
        }

        String thumbPath = "thumb_" + objectPath;

        String originalUrl = String.format("%s/%s/%s", CDN_BASE_URL, BUCKET_NAME, objectPath);
        String thumbnailUrl = String.format("%s/%s/%s", CDN_BASE_URL, BUCKET_NAME, thumbPath);

        return ImageResponseDTO.builder()
                .original(originalUrl)
                .thumbnail(thumbnailUrl)
                .build();
    }
}