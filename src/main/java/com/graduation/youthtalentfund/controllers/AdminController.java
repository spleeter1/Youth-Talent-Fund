package com.graduation.youthtalentfund.controllers;

import com.graduation.youthtalentfund.dtos.request.CreateStaffRequest;
import com.graduation.youthtalentfund.dtos.response.UserInfoDTO;
import com.graduation.youthtalentfund.entities.User;
import com.graduation.youthtalentfund.exceptions.ResourceNotFoundException;
import com.graduation.youthtalentfund.repositories.UserRepository;
import com.graduation.youthtalentfund.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/create-staff")
    public ResponseEntity<UserInfoDTO>  createStaff (@Valid  @RequestBody CreateStaffRequest request, Authentication authentication){
        String email = authentication.getName();
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        return ResponseEntity.ok(userService.createStaff(request, admin));
    }
}
