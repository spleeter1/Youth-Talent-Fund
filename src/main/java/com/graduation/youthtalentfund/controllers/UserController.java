package com.graduation.youthtalentfund.controllers;

import com.graduation.youthtalentfund.dtos.request.UpdateProfileDTO;
import com.graduation.youthtalentfund.dtos.response.UserInfoDTO;
import com.graduation.youthtalentfund.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PutMapping("/me")
    public ResponseEntity<UserInfoDTO> updateMyProfile(
            @Valid @RequestBody UpdateProfileDTO profileUpdateDTO,
            Authentication authentication) {

        String userEmail = authentication.getName();
        UserInfoDTO updatedUser = userService.updateProfile(profileUpdateDTO, userEmail);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/me/avatar")
    public ResponseEntity<UserInfoDTO> updateMyAvatar(
            @RequestParam("avatar") MultipartFile file,
            Authentication authentication) {
        String userEmail = authentication.getName();
        UserInfoDTO updatedUser = userService.updateAvatar(userEmail, file);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/me/password")
    public ResponseEntity<String> changeMyPassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO, Authentication authentication) {
        String userEmail = authentication.getName();
        userService.changePassword(userEmail, changePasswordDTO.getOldPassword(), changePasswordDTO.getNewPassword());
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }


}
