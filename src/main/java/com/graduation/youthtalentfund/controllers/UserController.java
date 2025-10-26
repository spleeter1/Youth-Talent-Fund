package com.graduation.youthtalentfund.controllers;

import com.graduation.youthtalentfund.dtos.request.UpdateProfileDTO;
import com.graduation.youthtalentfund.dtos.response.UserInfoDTO;
import com.graduation.youthtalentfund.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
