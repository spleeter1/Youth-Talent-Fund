package com.graduation.youthtalentfund.services;

import com.graduation.youthtalentfund.dtos.request.UpdateProfileDTO;
import com.graduation.youthtalentfund.dtos.response.UserInfoDTO;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserInfoDTO updateProfile(UpdateProfileDTO updateProfileDTO,String userEmail);
    UserInfoDTO updateAvatar(String userEmail,MultipartFile file);
}
