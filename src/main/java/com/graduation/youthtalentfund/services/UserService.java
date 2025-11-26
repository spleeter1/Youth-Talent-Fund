package com.graduation.youthtalentfund.services;

import com.graduation.youthtalentfund.dtos.request.CreateStaffRequest;
import com.graduation.youthtalentfund.dtos.request.UpdateProfileDTO;
import com.graduation.youthtalentfund.dtos.response.UserInfoDTO;
import com.graduation.youthtalentfund.entities.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserInfoDTO updateProfile(UpdateProfileDTO updateProfileDTO,String userEmail);
    UserInfoDTO updateAvatar(String userEmail,MultipartFile file);
    void changePassword(String userEmail, String oldPassword, String newPassword);

    //Admin
    UserInfoDTO createStaff(CreateStaffRequest request, User admin);
}
