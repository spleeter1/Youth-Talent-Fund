package com.graduation.youthtalentfund.services;

import com.graduation.youthtalentfund.dtos.request.CreateProofReportDTO;
import com.graduation.youthtalentfund.dtos.response.ProofReportDetailDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ProofReportService {
    ProofReportDetailDTO createProofReport(String campaignCode, CreateProofReportDTO createProofReportDTO, MultipartFile[] files);
}
