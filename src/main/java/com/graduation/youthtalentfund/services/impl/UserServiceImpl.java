package com.graduation.youthtalentfund.services.impl;

import com.graduation.youthtalentfund.dtos.request.UpdateProfileDTO;
import com.graduation.youthtalentfund.dtos.response.UserInfoDTO;
import com.graduation.youthtalentfund.entities.User;
import com.graduation.youthtalentfund.exceptions.ResourceNotFoundException;
import com.graduation.youthtalentfund.repositories.UserRepository;
import com.graduation.youthtalentfund.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserInfoDTO updateProfile(UpdateProfileDTO updateProfileDTO, String userEmail) {
        User curUser = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        if (StringUtils.hasText(updateProfileDTO.getFullName())) curUser.setFullName(updateProfileDTO.getFullName());
        if (updateProfileDTO.getAddress() != null) curUser.setAddress(updateProfileDTO.getAddress());
        if (updateProfileDTO.getPhoneNumber() != null) curUser.setPhoneNumber(updateProfileDTO.getPhoneNumber());
        if (updateProfileDTO.getBio() != null) curUser.setBio(updateProfileDTO.getBio());

        User updatedUser = userRepository.save(curUser);


        List<String> roles = updatedUser.getUserRoles().stream()
                .map(updatedUserRole -> updatedUserRole.getRole().getName())
                .toList();
        return UserInfoDTO.builder()
                .code(updatedUser.getCode())
                .fullName(updatedUser.getFullName())
                .email(updatedUser.getEmail())
                .avatarPath(updatedUser.getAvatarPath())
                .address(updatedUser.getAddress())
                .roles(roles)
                .build();
    }
}