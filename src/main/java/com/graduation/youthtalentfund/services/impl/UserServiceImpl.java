package com.graduation.youthtalentfund.services.impl;

import com.graduation.youthtalentfund.dtos.request.UpdateProfileDTO;
import com.graduation.youthtalentfund.dtos.response.AvatarPathsDTO;
import com.graduation.youthtalentfund.dtos.response.UserInfoDTO;
import com.graduation.youthtalentfund.entities.User;
import com.graduation.youthtalentfund.exceptions.BadRequestException;
import com.graduation.youthtalentfund.exceptions.ResourceNotFoundException;
import com.graduation.youthtalentfund.repositories.UserRepository;
import com.graduation.youthtalentfund.services.FileStorageService;
import com.graduation.youthtalentfund.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;

    @Value("${cdn.base-url}")
    private String cdnBaseUrl;

    @Value("${minio.bucket-name}")
    private String bucketName;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif");
    private static final long MAX_FILE_SIZE = 15 * 1024 * 1024;

    @Override
    @Transactional
    public UserInfoDTO updateProfile(UpdateProfileDTO updateProfileDTO, String userEmail) {
        User curUser = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        if (StringUtils.hasText(updateProfileDTO.getFullName())) curUser.setFullName(updateProfileDTO.getFullName());
        if (updateProfileDTO.getAddress() != null) curUser.setAddress(updateProfileDTO.getAddress());
        if (updateProfileDTO.getPhoneNumber() != null) curUser.setPhoneNumber(updateProfileDTO.getPhoneNumber());
        if (updateProfileDTO.getBio() != null) curUser.setBio(updateProfileDTO.getBio());

        User updatedUser = userRepository.save(curUser);
        return mapUserToUserInfoDTO(updatedUser);
    }

    @Override
    @Transactional
    public UserInfoDTO updateAvatar(String userEmail, MultipartFile file) {
        validateAvatarFile(file);

        User user = userRepository.findByEmailWithRoles(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        if (StringUtils.hasText(user.getAvatarPath())) {
            fileStorageService.deleteFile(user.getAvatarPath());
        }

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String objectName = String.format("users/%s/%s.%s", user.getCode(), UUID.randomUUID(), extension);

        Map<String, String> storedObjects = fileStorageService.storeFile(file, objectName);

        user.setAvatarPath(storedObjects.get("original"));
        User updatedUser = userRepository.save(user);

        return mapUserToUserInfoDTO(updatedUser);
    }

    @Override
    @Transactional
    public void changePassword(String userEmail, String oldPassword, String newPassword) {
        User currentUser = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User","email",userEmail));
        if(!passwordEncoder.matches(oldPassword,currentUser.getPassword())){
            throw new BadRequestException("Mật khẩu cũ không chính xác.");
        }
        if (passwordEncoder.matches(newPassword, currentUser.getPassword())) {
            throw new BadRequestException("Mật khẩu mới không được trùng với mật khẩu cũ.");
        }

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);
    }

    private void validateAvatarFile(MultipartFile file) {
        if (file.isEmpty()) throw new BadRequestException("Vui lòng chọn một file để tải lên.");
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType()))
            throw new BadRequestException("Chỉ chấp nhận các định dạng file ảnh (JPEG, PNG, GIF).");
        if (file.getSize() > MAX_FILE_SIZE) throw new BadRequestException("Kích thước file không được vượt quá 5MB.");
    }

    private UserInfoDTO mapUserToUserInfoDTO(User user) {
        AvatarPathsDTO avatarPaths = null;
        if (StringUtils.hasText(user.getAvatarPath())) {
            String originalObjectName = user.getAvatarPath();
            // Tên thumbnail được quy ước có prefix "thumb_"
            String thumbnailObjectName = "thumb_" + originalObjectName;

            // Xây dựng URL đầy đủ theo cấu trúc CDN: {cdn.base-url}/{bucket-name}/{object-name}
            avatarPaths = AvatarPathsDTO.builder()
                    .original(String.format("%s/%s/%s", cdnBaseUrl, bucketName, originalObjectName))
                    .thumbnail(String.format("%s/%s/%s", cdnBaseUrl, bucketName, thumbnailObjectName))
                    .build();
        }

        List<String> roles = user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getName())
                .toList();

        return UserInfoDTO.builder()
                .code(user.getCode())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .avatarPaths(avatarPaths)
                .address(user.getAddress())
                .bio(user.getBio())
                .roles(roles)
                .build();
    }
}