package com.graduation.youthtalentfund.services;

import com.graduation.youthtalentfund.dtos.request.CreateProofReportDTO;
import com.graduation.youthtalentfund.dtos.response.ProofReportDetailDTO;
import com.graduation.youthtalentfund.enums.ProofReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;


public interface ProofReportService {
    ProofReportDetailDTO createProofReport(String campaignCode, CreateProofReportDTO createProofReportDTO, MultipartFile[] files);
    Page<ProofReportDetailDTO> getProofReportsByCampaign(String campaignCode, ProofReportType type, Pageable pageable);
}
