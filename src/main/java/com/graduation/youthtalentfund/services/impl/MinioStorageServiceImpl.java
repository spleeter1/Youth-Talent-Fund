package com.graduation.youthtalentfund.services.impl;

import com.graduation.youthtalentfund.constants.MessageConstants;
import com.graduation.youthtalentfund.exceptions.FileUploadException;
import com.graduation.youthtalentfund.services.FileStorageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MinioStorageServiceImpl implements FileStorageService {

    private final MinioClient minioClient;
    private static final Logger logger = LoggerFactory.getLogger(MinioStorageServiceImpl.class);

    @Value("${minio.bucket-name}")
    private String bucketName;

    public static final String THUMBNAIL_PREFIX = "thumb_";

    private final S3Presigner s3Presigner;

    @Override
    public Map<String, String> storeFile(MultipartFile file, String objectName) {
        try {
            // Upload file gốc
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .headers(Map.of("x-amz-acl", "public-read"))
                            .build()
            );
            Map<String, String> storedObjects = new HashMap<>();
            storedObjects.put("original", objectName);
            // Tạo và upload thumbnail nếu là ảnh
            if (file.getContentType() != null && file.getContentType().startsWith("image/")) {
                String thumbnailObjectName = THUMBNAIL_PREFIX + objectName;
                try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                    Thumbnails.of(file.getInputStream()).size(200, 200).toOutputStream(os);
                    try (InputStream thumbnailInputStream = new ByteArrayInputStream(os.toByteArray())) {
                        minioClient.putObject(
                                PutObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(thumbnailObjectName)
                                        .stream(thumbnailInputStream, os.size(), -1)
                                        .contentType(file.getContentType())
                                        .build()
                        );
                    }
                }

                storedObjects.put("thumbnail", thumbnailObjectName);
            }
            return storedObjects;
        } catch (Exception e) {
            logger.error("Lỗi khi upload file lên MinIO", e);
            throw new FileUploadException(MessageConstants.FILE_UPLOAD_ERROR);
        }
    }

    @Override
    public void deleteFile(String baseObjectName) {
        if (!StringUtils.hasText(baseObjectName)) return;
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(baseObjectName).build()
            );

            try {
                minioClient.statObject(
                        io.minio.StatObjectArgs.builder()
                                .bucket(bucketName)
                                .object(THUMBNAIL_PREFIX + baseObjectName)
                                .build());

                minioClient.removeObject(
                        RemoveObjectArgs.builder().bucket(bucketName).object(THUMBNAIL_PREFIX + baseObjectName).build()
                );
            } catch (Exception ignore) {

            }
        } catch (Exception e) {
            logger.error("Lỗi khi xóa object {} từ MinIO", baseObjectName, e);
        }
    }


    @Override
    public String generatePresignedDownloadUrl(String objectKey, Duration duration) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(duration)
                        .getObjectRequest(getObjectRequest)
                        .build();

        PresignedGetObjectRequest presignedRequest =
                s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }
}