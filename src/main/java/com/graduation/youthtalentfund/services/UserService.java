package com.graduation.youthtalentfund.services;

import com.graduation.youthtalentfund.dtos.request.UpdateProfileDTO;
import com.graduation.youthtalentfund.dtos.response.UserInfoDTO;

public interface UserService {
    UserInfoDTO updateProfile(UpdateProfileDTO updateProfileDTO,String userEmail);
}
