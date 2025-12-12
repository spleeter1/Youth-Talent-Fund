package com.graduation.youthtalentfund.services.impl;

import com.graduation.youthtalentfund.dtos.request.CreateProofReportDTO;
import com.graduation.youthtalentfund.dtos.response.ProofReportDetailDTO;
import com.graduation.youthtalentfund.dtos.response.donate.DonationRptDTO;
import com.graduation.youthtalentfund.entities.Attachment;
import com.graduation.youthtalentfund.entities.Campaign;
import com.graduation.youthtalentfund.entities.ProofReport;
import com.graduation.youthtalentfund.entities.User;
import com.graduation.youthtalentfund.enums.ProofReportType;
import com.graduation.youthtalentfund.exceptions.BadRequestException;
import com.graduation.youthtalentfund.exceptions.ResourceNotFoundException;
import com.graduation.youthtalentfund.repositories.AttachmentRepository;
import com.graduation.youthtalentfund.repositories.CampaignRepository;
import com.graduation.youthtalentfund.repositories.ProofReportRepository;
import com.graduation.youthtalentfund.repositories.UserRepository;
import com.graduation.youthtalentfund.services.DonationService;
import com.graduation.youthtalentfund.services.FileStorageService;
import com.graduation.youthtalentfund.services.ProofReportService;
import com.graduation.youthtalentfund.utils.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProofReportServiceImpl implements ProofReportService {

    private static final Set<String> ALLOWED_TYPES = Set.of("application/pdf", "image/pdf", "image/jpeg", "image/jpg", "image/png");
    private static final long MAX_FILE_SIZE = 15 * 1024 * 1024;
    private static final int MAX_FILES = 5;

    private final UserRepository userRepository;
    private final ProofReportRepository proofReportRepository;
    private final CampaignRepository campaignRepository;
    private final FileStorageService fileStorageService;
    private final AttachmentRepository attachmentRepository;
    private final DonationService donationService;

    private void validateFiles(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new BadRequestException("Phải upload ít nhất 1 file.");
        }
        if (files.length > MAX_FILES) {
            throw new BadRequestException("Tối đa " + MAX_FILES + " file.");
        }
        for (MultipartFile file : files) {
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new BadRequestException(file.getOriginalFilename() + " vượt quá giới hạn " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
            }
            String ct = file.getContentType();
            if (ct == null || !ALLOWED_TYPES.contains(ct.toLowerCase())) {
                throw new BadRequestException("File không hợp lệ (chỉ PDF/JPG/PNG): " + file.getOriginalFilename());
            }
        }
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Lỗi xác thực: Không tìm thấy thông tin người dùng."));
    }

    @Override
    @Transactional
    public ProofReportDetailDTO createProofReport(String campaignCode, CreateProofReportDTO createProofReportDTO, MultipartFile[] files) {
        validateFiles(files);
        User author = getCurrentUser();
        Campaign campaign = campaignRepository.findByCode(campaignCode)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chiến dịch"));

        //check type
        ProofReportType proofReportType = createProofReportDTO.getType();
        DonationRptDTO transaction = donationService.createDonationRpt(createProofReportDTO.getCreateDonationRptDTO(), campaign, proofReportType);

        //save
        ProofReport proofReport = ProofReport.builder()
                .code(CodeGenerator.generateReportCode())
                .title(createProofReportDTO.getTitle())
                .content(createProofReportDTO.getContent())
                .type(createProofReportDTO.getType())
                .author(author)
                .campaign(campaign)
                .build();
        proofReportRepository.save(proofReport);

        List<Attachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String objectName = String.format(
                    "reports/%s/%s.%s",
                    proofReport.getCode(),
                    UUID.randomUUID(),
                    ext
            );

            Map<String, String> stored = fileStorageService.storeFile(file, objectName);

            Attachment attachment = Attachment.builder()
                    .originalFilename(file.getOriginalFilename())
                    .filePath(stored.get("original"))
                    .proofReport(proofReport)
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .build();

            attachments.add(attachment);
        }
        attachmentRepository.saveAll(attachments);
        return ProofReportDetailDTO.from(proofReport, attachments, transaction);
    }
}
