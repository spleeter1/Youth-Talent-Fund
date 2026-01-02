package com.graduation.youthtalentfund.services;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileStorageService {
    Map<String,String> storeFile(MultipartFile file, String objectName);
    void deleteFile(String baseObjectName);
}
