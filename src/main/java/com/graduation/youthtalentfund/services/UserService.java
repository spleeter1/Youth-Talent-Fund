package com.graduation.youthtalentfund.services;

import com.graduation.youthtalentfund.dtos.request.AdminChangePasswordRequest;
import com.graduation.youthtalentfund.dtos.request.CreateStaffRequest;
import com.graduation.youthtalentfund.dtos.request.UpdateProfileDTO;
import com.graduation.youthtalentfund.dtos.response.UserInfoDTO;
import com.graduation.youthtalentfund.entities.User;
import com.graduation.youthtalentfund.repositories.Projection.StaffProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserInfoDTO updateProfile(UpdateProfileDTO updateProfileDTO, String userEmail);
    UserInfoDTO updateAvatar(String userEmail, MultipartFile file);
    void changePassword(String userEmail, String oldPassword, String newPassword);

    //get
    UserInfoDTO getUserInfo(String value);

    //Admin
    UserInfoDTO createStaff(CreateStaffRequest request, User admin);
    Page<StaffProjection> getStaffs(String keyword, Pageable pageable);
    void lockUser(String targetEmail);
    void unlockUser(String targetEmail);
    void deleteUser(String targetEmail);
    void changeStaffPassword(String targetEmail, AdminChangePasswordRequest request);

}
