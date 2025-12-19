package com.graduation.youthtalentfund.services.impl;

import com.graduation.youthtalentfund.entities.Attachment;
import com.graduation.youthtalentfund.exceptions.ResourceNotFoundException;
import com.graduation.youthtalentfund.repositories.AttachmentRepository;
import com.graduation.youthtalentfund.services.AttachmentService;
import com.graduation.youthtalentfund.services.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final FileStorageService fileStorageService;

    @Override
    public String downloadAttachment(Long id) {
        Attachment attachment = attachmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài liệu"));
        String objectKey = attachment.getFilePath();

        return fileStorageService.generatePresignedDownloadUrl(objectKey, Duration.ofMinutes(10));
    }
}
