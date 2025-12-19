package com.graduation.youthtalentfund.controllers;

import com.graduation.youthtalentfund.services.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;

    @GetMapping("/public/attachments/{id}/download")
    public ResponseEntity<?> downloadFile(@PathVariable Long id){
        String downloadURL = attachmentService.downloadAttachment(id);
        return ResponseEntity.ok(downloadURL);
    }
}
