package com.graduation.youthtalentfund.dtos.response;

import com.graduation.youthtalentfund.dtos.response.donate.DonationDataResponse;
import com.graduation.youthtalentfund.dtos.response.donate.DonationRptDTO;
import com.graduation.youthtalentfund.entities.Attachment;
import com.graduation.youthtalentfund.entities.ProofReport;
import com.graduation.youthtalentfund.enums.ProofReportType;
import com.graduation.youthtalentfund.utils.FileUtils;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProofReportDetailDTO {
    private String code;
    private String title;
    private String content;
    private ProofReportType type;
    private AuthorDTO author;
    private List<AttachmentDTO> attachments;
    private DonationRptDTO transaction;

    @Data
    @Builder
    public static class AuthorDTO {
        private String code;
        private String email;
        private String fullName;
        private FileUrlResponseDTO avatarUrls;
        private String phoneNumber;
    }

    @Data
    @Builder
    public static class AttachmentDTO {
        private Long id;
        private String originalFilename;
        private FileUrlResponseDTO fileUrl;
        private String fileType;
        private Long fileSize;
    }

    public static ProofReportDetailDTO from(ProofReport report, List<Attachment> attachments, DonationRptDTO transaction) {

        ProofReportDetailDTO.AuthorDTO authorDTO = ProofReportDetailDTO.AuthorDTO.builder()
                .code(report.getAuthor().getCode())
                .email(report.getAuthor().getEmail())
                .fullName(report.getAuthor().getFullName())
                .phoneNumber(report.getAuthor().getPhoneNumber())
                .avatarUrls(FileUtils.build(report.getAuthor().getAvatarPath()))
                .build();

        List<ProofReportDetailDTO.AttachmentDTO> attachmentDTOs = attachments.stream()
                .map(a -> ProofReportDetailDTO.AttachmentDTO.builder()
                        .id(a.getId())
                        .originalFilename(a.getOriginalFilename())
                        .fileUrl(FileUtils.buildFile(a.getFilePath()))
                        .fileType(a.getFileType())
                        .fileSize(a.getFileSize())
                        .build())
                .toList();

        return ProofReportDetailDTO.builder()
                .code(report.getCode())
                .title(report.getTitle())
                .content(report.getContent())
                .type(report.getType())
                .author(authorDTO)
                .attachments(attachmentDTOs)
                .transaction(transaction)
                .build();
    }
}
