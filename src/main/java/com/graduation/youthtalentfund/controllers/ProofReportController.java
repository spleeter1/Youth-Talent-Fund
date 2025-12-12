package com.graduation.youthtalentfund.controllers;

import com.graduation.youthtalentfund.dtos.request.CreateProofReportDTO;
import com.graduation.youthtalentfund.dtos.response.ProofReportDetailDTO;
import com.graduation.youthtalentfund.services.ProofReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProofReportController {
    private final ProofReportService proofReportService;

    @PostMapping(value = "/management/{campaignCode}/reports", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProofReportDetailDTO> createProofReport(
            @PathVariable String campaignCode,
            @Valid @RequestPart("data") CreateProofReportDTO createProofReportDTO,
            @RequestPart(value = "files", required = false) MultipartFile[] files
    ) {
        ProofReportDetailDTO response =
                proofReportService.createProofReport(campaignCode, createProofReportDTO, files);

        return ResponseEntity.ok(response);
    }
}
