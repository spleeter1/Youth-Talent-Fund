package com.graduation.youthtalentfund.services.impl;

import com.graduation.youthtalentfund.dtos.request.CampaignStatisticRequest;
import com.graduation.youthtalentfund.dtos.request.CreateCampaignDTO;
import com.graduation.youthtalentfund.dtos.request.UpdateCampaignDTO;
import com.graduation.youthtalentfund.dtos.response.CampaignCountResponse;
import com.graduation.youthtalentfund.dtos.response.CampaignDetailDTO;
import com.graduation.youthtalentfund.dtos.response.CampaignStatisticResponse;
import com.graduation.youthtalentfund.entities.Campaign;
import com.graduation.youthtalentfund.entities.CustomUserDetails;
import com.graduation.youthtalentfund.entities.User;
import com.graduation.youthtalentfund.enums.CampaignStatus;
import com.graduation.youthtalentfund.exceptions.BadRequestException;
import com.graduation.youthtalentfund.exceptions.ResourceNotFoundException;
import com.graduation.youthtalentfund.repositories.CampaignRepository;
import com.graduation.youthtalentfund.repositories.Projection.CampaignCountProjection;
import com.graduation.youthtalentfund.repositories.Projection.CampaignDetailProjection;
import com.graduation.youthtalentfund.repositories.Projection.CampaignShortProjection;
import com.graduation.youthtalentfund.repositories.Projection.CampaignStatisticProjection;
import com.graduation.youthtalentfund.repositories.UserRepository;
import com.graduation.youthtalentfund.services.CampaignService;
import com.graduation.youthtalentfund.services.FileStorageService;
import com.graduation.youthtalentfund.utils.AuthUtil;
import com.graduation.youthtalentfund.utils.CodeGenerator;
import com.graduation.youthtalentfund.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final CodeGenerator codeGenerator;

    @Value("${cdn.base-url}")
    private String cdnBaseUrl;

    @Value("${minio.bucket-name}")
    private String bucketName;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif");
    private static final long MAX_FILE_SIZE = 15 * 1024 * 1024;

    @Override
    public Page<CampaignShortProjection> searchCampaigns(String status, String category, String keyword, int page, int size) {
        return campaignRepository.findAllCampaignsShort(status, category, keyword, PageRequest.of(page, size));
    }

    @Override
    public Page<CampaignShortProjection> getMyCampaigns(
            CustomUserDetails userDetails,
            String status,
            String category,
            String keyword,
            Pageable pageable
    ) {
        return campaignRepository.findManagedCampaignsShort(
                userDetails.getId(),
                status,
                category,
                keyword,
                pageable
        );
    }

    @Override
    public CampaignDetailProjection getByCodeOrSlug(String value) {
        return campaignRepository.findByCodeOrSlug(value).orElseThrow(() -> new ResourceNotFoundException("Campaign Not Found"));
    }

    @Transactional
    @Override
    public CampaignDetailDTO createCampaign(CreateCampaignDTO request, MultipartFile image) {
        User currentUser = getCurrentUser();

        User assignee = determineAssignee(currentUser, request.getAssigneeCode());

        String generatedCode = CodeGenerator.generateCampaignCode();

        String storedPath = null;

        if (image != null && !image.isEmpty()) {
            if (!ALLOWED_IMAGE_TYPES.contains(image.getContentType()))
                throw new BadRequestException("Chỉ chấp nhận các định dạng image ảnh (JPEG, PNG, GIF).");
            if (image.getSize() > MAX_FILE_SIZE)
                throw new BadRequestException("Kích thước image không được vượt quá 15MB.");

            String extension = StringUtils.getFilenameExtension(image.getOriginalFilename());

            String objectName = String.format("campaigns/%s/%s.%s",
                    generatedCode,
                    UUID.randomUUID(),
                    extension);

            Map<String, String> uploadResult = fileStorageService.storeFile(image, objectName);
            storedPath = uploadResult.get("original");
        }

        CampaignStatus startStatus = request.getStartDate().isAfter(LocalDateTime.now())
                ? CampaignStatus.PENDING
                : CampaignStatus.IN_PROGRESS;

        Campaign campaign = Campaign.builder()
                .title(request.getTitle())
                .slug(codeGenerator.generateUniqueSlug(request.getTitle()))
                .story(request.getStory())
                .code(generatedCode)
                .description(request.getDescription())
                .targetAmount(request.getTargetAmount())
                .category(request.getCategory())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .coverImagePath(storedPath)
                .status(startStatus)
                .currentAmount(BigDecimal.ZERO)
                .location(request.getLocation())
                .staff(assignee)
                .build();

        Campaign saved = campaignRepository.save(campaign);
        return mapCampaignToDTO(saved);
    }

    @Override
    @Transactional
    public CampaignDetailDTO updateCampaign(String code, UpdateCampaignDTO updateCampaignDTO, MultipartFile image) {

        Campaign campaign = campaignRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign không tồn tại"));

        User currentUser = getCurrentUser();
        boolean isAdmin = currentUser.getUserRoles().stream()
                .anyMatch(role -> role.getRole().getName().equals("ADMIN"));

        if (!isAdmin && !campaign.getStaff().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Bạn không có quyền cập nhật chiến dịch này");
        }

        CampaignStatus status = campaign.getStatus();

        boolean canEditFull = status == CampaignStatus.PENDING;
        boolean canEditPartial = status == CampaignStatus.IN_PROGRESS || status == CampaignStatus.ON_HOLD;

        if (!canEditFull && !canEditPartial) {
            throw new BadRequestException("Chiến dịch ở trạng thái " + status + " không được chỉnh sửa.");
        }
        if (updateCampaignDTO.getAssigneeCode() != null) {
            User assignee = determineAssignee(currentUser, updateCampaignDTO.getAssigneeCode());
            campaign.setStaff(assignee);
        }

        if (updateCampaignDTO.getTargetAmount() != null) {
            updateCampaignGoal(campaign, updateCampaignDTO.getTargetAmount());
        }

        if (updateCampaignDTO.getEndDate() != null) {
            if (updateCampaignDTO.getEndDate().isBefore(LocalDateTime.now().plusDays(1))) {
                throw new IllegalArgumentException("endDate phải lớn hơn hiện tại ít nhất 1 ngày");
            }
            if (updateCampaignDTO.getEndDate().isBefore(campaign.getStartDate().plusDays(7))) {
                throw new IllegalArgumentException("endDate phải lớn hơn startDate ít nhất 7 ngày");
            }
            campaign.setEndDate(updateCampaignDTO.getEndDate());
        }

        if (canEditFull) {
            if (updateCampaignDTO.getTitle() != null) {
                campaign.setTitle(updateCampaignDTO.getTitle());
                campaign.setSlug(codeGenerator.generateUniqueSlug(updateCampaignDTO.getTitle()));
            }
            if (updateCampaignDTO.getDescription() != null) {
                campaign.setDescription(updateCampaignDTO.getDescription());
            }
            if (updateCampaignDTO.getLocation() != null) {
                campaign.setLocation(updateCampaignDTO.getLocation());
            }
            if (updateCampaignDTO.getStory() != null) {
                campaign.setStory(updateCampaignDTO.getStory());
            }

            if (updateCampaignDTO.getStartDate() != null &&
                    updateCampaignDTO.getStartDate().isBefore(LocalDateTime.now())) {
                throw new BadRequestException("StartDate không được nhỏ hơn thời điểm hiện tại.");
            } else campaign.setStartDate(updateCampaignDTO.getStartDate());

            if (updateCampaignDTO.getEndDate() != null) {
                campaign.setEndDate(updateCampaignDTO.getEndDate());
            }
            if (updateCampaignDTO.getCategory() != null) {
                campaign.setCategory(updateCampaignDTO.getCategory());
            }
        }
        if (image != null && !image.isEmpty()) {
            if (!ALLOWED_IMAGE_TYPES.contains(image.getContentType()))
                throw new BadRequestException("Chỉ chấp nhận các định dạng ảnh (JPEG, PNG, GIF).");
            if (image.getSize() > MAX_FILE_SIZE)
                throw new BadRequestException("Kích thước ảnh không được vượt quá 15MB.");

            if (campaign.getCoverImagePath() != null) {
                fileStorageService.deleteFile(campaign.getCoverImagePath());
            }

            String extension = StringUtils.getFilenameExtension(image.getOriginalFilename());
            String objectName = String.format("campaigns/%s/%s.%s",
                    campaign.getCode(),
                    UUID.randomUUID(),
                    extension);

            Map<String, String> uploadResult = fileStorageService.storeFile(image, objectName);
            campaign.setCoverImagePath(uploadResult.get("original"));
        }

        Campaign updated = campaignRepository.save(campaign);

        return mapCampaignToDTO(updated);
    }

    public void updateCampaignGoal(Campaign campaign, BigDecimal newGoal) {
        BigDecimal totalRaised = campaign.getCurrentAmount();

        if (newGoal.compareTo(totalRaised) < 0) {
            throw new IllegalArgumentException("Mục tiêu mới không thể nhỏ hơn số tiền đã quyên góp (" + totalRaised + ").");
        }
        campaign.setTargetAmount(newGoal);
    }

    @Override
    @Transactional
    public CampaignDetailDTO updateCampaignStatus(String code, CampaignStatus newStatus) {
        Campaign campaign = campaignRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign không tồn tại"));

        User currentUser = getCurrentUser();
        boolean isAdmin = currentUser.getUserRoles().stream()
                .anyMatch(role -> role.getRole().getName().equals("ADMIN"));

        if (!isAdmin && !campaign.getStaff().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Bạn không có quyền cập nhật chiến dịch này");
        }

        CampaignStatus current = campaign.getStatus();
        if (!current.canTransitionTo(newStatus)) {
            throw new IllegalStateException("Không thể chuyển trạng thái từ " + current + " sang " + newStatus);
        }

        LocalDateTime now = LocalDateTime.now();

        if (current == CampaignStatus.PENDING && newStatus == CampaignStatus.IN_PROGRESS) {
            if (campaign.getStartDate() == null || campaign.getStartDate().isAfter(now)) {
                campaign.setStartDate(now);
            }
        }

        if ((current == CampaignStatus.IN_PROGRESS || current == CampaignStatus.ON_HOLD)
                && newStatus == CampaignStatus.COMPLETED) {
            if (campaign.getEndDate() == null || campaign.getEndDate().isAfter(now)) {
                LocalDateTime startDate = campaign.getStartDate() != null ? campaign.getStartDate() : now;
                campaign.setEndDate(now.isAfter(startDate) ? now : startDate);
            }
        }
        campaign.setStatus(newStatus);
        Campaign updated = campaignRepository.save(campaign);

        return mapCampaignToDTO(updated);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Page<CampaignShortProjection> getCampaignsByStaffId(
            String staffCode,
            String status,
            String category,
            String keyword,
            Pageable pageable) {

        User staff = userRepository.findByCode(staffCode).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin người dùng."));

        return campaignRepository.findManagedCampaignsShort(staff.getId(), status, category, keyword, pageable);
    }

    @Override
    public CampaignStatus determineStatus(Campaign campaign) {
        LocalDateTime now = LocalDateTime.now();
        if (campaign.getStaff() == null) {
            return CampaignStatus.ON_HOLD;
        }
        if (campaign.getEndDate().isBefore(now)) {
            return CampaignStatus.COMPLETED;
        }
        if (campaign.getStartDate().isAfter(now)) {
            return CampaignStatus.PENDING;
        }
        if (campaign.getStartDate().isBefore(now) && campaign.getEndDate().isAfter(now)) {
            return CampaignStatus.IN_PROGRESS;
        }
        return campaign.getStatus();
    }

    private User determineAssignee(User currentUser, String assigneeCode) {
        boolean isAdmin = currentUser.getUserRoles().stream()
                .anyMatch(role -> role.getRole().getName().equals("ADMIN"));
        if (isAdmin) {
            if (assigneeCode != null && !assigneeCode.isBlank()) {
                User assignee = userRepository.findByCode(assigneeCode)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với mã: " + assigneeCode));

                boolean isStaff = assignee.getUserRoles().stream()
                        .anyMatch(role -> role.getRole().getName().equals("STAFF"));

                if (!isStaff) {
                    throw new BadRequestException("Người được phân công không phải STAFF");
                }

                return assignee;
            }
            return currentUser;
        }
        return currentUser;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Lỗi xác thực: Không tìm thấy thông tin người dùng."));
    }

    private CampaignDetailDTO mapCampaignToDTO(Campaign campaign) {
        CampaignDetailDTO.StaffInfoDTO staffDTO = null;
        if (campaign.getStaff() != null) {
            User s = campaign.getStaff();
            staffDTO = CampaignDetailDTO.StaffInfoDTO.builder()
                    .fullName(s.getFullName())
                    .code(s.getCode())
                    .email(s.getEmail())
                    .status(s.getStatus())
                    .avatar(FileUtils.build(s.getAvatarPath()))
                    .build();
        }
        return CampaignDetailDTO.builder()
                .code(campaign.getCode())
                .title(campaign.getTitle())
                .slug(campaign.getSlug())
                .description(campaign.getDescription())
                .location(campaign.getLocation())
                .story(campaign.getStory())
                .currentAmount(campaign.getCurrentAmount())
                .targetAmount(campaign.getTargetAmount())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .createdAt(campaign.getCreatedAt())
                .category(campaign.getCategory())
                .status(campaign.getStatus())
                .coverImage(FileUtils.build(campaign.getCoverImagePath()))
                .assignee(staffDTO)
                .build();
    }

    @Override
    public Page<CampaignStatisticResponse> getCampaignStatistic(CampaignStatisticRequest request) {
        Page<CampaignStatisticProjection> projections = campaignRepository.getCampaignStatistic(request.getCampaignCode(), request.getFromDate(), request.getToDate(), Pageable.ofSize(20).withPage(request.getPageNumber()));
        return projections.map(projection -> {
            CampaignStatisticResponse response = new CampaignStatisticResponse();
            response.setCampaignCode(projection.getCampaignCode());
            response.setStaffCode(projection.getStaffCode());
            response.setTitle(projection.getTitle());
            response.setDonationCount(projection.getDonationCount());
            response.setTotalReceived(projection.getTotalReceived());
            return response;
        });
    }

    public CampaignCountResponse getCampaignCount(LocalDateTime fromDate, LocalDateTime toDate) {
        CampaignCountProjection projection = campaignRepository.getCampaignCount(fromDate, toDate);
        CampaignCountResponse response = new CampaignCountResponse();
        response.setActiveCampaign(projection.getActiveCampaign());
        response.setTotalCampaign(projection.getTotalCampaign());
        response.setActiveCampaign(projection.getTotalCampaign());
        return response;
    }
}
