package com.graduation.youthtalentfund.services.impl;

import com.graduation.youthtalentfund.dtos.request.CreateCampaignDTO;
import com.graduation.youthtalentfund.entities.Campaign;
import com.graduation.youthtalentfund.entities.User;
import com.graduation.youthtalentfund.enums.CampaignCategory;
import com.graduation.youthtalentfund.enums.CampaignStatus;
import com.graduation.youthtalentfund.exceptions.ResourceNotFoundException;
import com.graduation.youthtalentfund.repositories.CampaignRepository;
import com.graduation.youthtalentfund.repositories.Projection.CampaignDetailProjection;
import com.graduation.youthtalentfund.repositories.Projection.CampaignShortProjection;
import com.graduation.youthtalentfund.repositories.UserRepository;
import com.graduation.youthtalentfund.services.CampaignService;
import com.graduation.youthtalentfund.services.FileStorageService;
import com.graduation.youthtalentfund.utils.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Override
    public Page<CampaignShortProjection> searchCampaigns(String status, String category, String keyword, int page, int size) {
        return campaignRepository.findAllCampaignsShort(status, category, keyword, PageRequest.of(page, size));
    }

    @Override
    public CampaignDetailProjection getByCodeOrSlug(String value) {
        return campaignRepository.findByCodeOrSlug(value).orElseThrow(() -> new ResourceNotFoundException("Campaign Not Found"));
    }

    @Transactional
    @Override
    public Campaign createCampaign(CreateCampaignDTO request) {
        User currentUser = getCurrentUser();

        User assignee = determineAssignee(currentUser, request.getAssigneeCode());

        //String imagePath = fileStorageService.storeFile(request.getCoverImage());

        // 4. Build Campaign
        Campaign campaign = Campaign.builder()
                .title(request.getTitle())
                .slug(generateUniqueSlug(request.getTitle()))
                .code(CodeGenerator.generateCampaignCode())
                .description(request.getDescription())
                .targetAmount(request.getTargetAmount())
                .category(request.getCategory())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .coverImagePath(imagePath)
                .status(CampaignStatus.PENDING)
                .currentAmount(BigDecimal.ZERO)

                .staff(assignee)
                .createdBy(currentUser.getId())
                .build();

        return campaignRepository.save(campaign);
    }

    private User determineAssignee(User currentUser, String assigneeCode) {
        boolean isAdmin = currentUser.getUserRoles().stream()
                .anyMatch(role -> role.getRole().getName().equals("ADMIN"));
        if (isAdmin) {
            if (assigneeCode != null && !assigneeCode.isBlank()) {
                return userRepository.findByCode(assigneeCode)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với mã: " + assigneeCode));
            }
            return currentUser;
        }
        return currentUser;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Lỗi xác thực: Không tìm thấy thông tin người dùng."));
    }
}
