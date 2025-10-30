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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
                            .build()
            );

            // Tạo và upload thumbnail
            String thumbnailObjectName = THUMBNAIL_PREFIX + objectName;
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                Thumbnails.of(file.getInputStream()).size(200, 200).toOutputStream(os);
                try(InputStream thumbnailInputStream = new ByteArrayInputStream(os.toByteArray())) {
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

            Map<String, String> storedObjects = new HashMap<>();
            storedObjects.put("original", objectName);
            storedObjects.put("thumbnail", thumbnailObjectName);
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
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(THUMBNAIL_PREFIX + baseObjectName).build()
            );
        } catch (Exception e) {
            logger.error("Lỗi khi xóa object {} từ MinIO", baseObjectName, e);
        }
    }
}