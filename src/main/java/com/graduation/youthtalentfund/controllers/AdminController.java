package com.graduation.youthtalentfund.controllers;

import com.graduation.youthtalentfund.dtos.request.CreateStaffRequest;
import com.graduation.youthtalentfund.dtos.request.UpdateProfileDTO;
import com.graduation.youthtalentfund.dtos.response.UserInfoDTO;
import com.graduation.youthtalentfund.entities.User;
import com.graduation.youthtalentfund.exceptions.ResourceNotFoundException;
import com.graduation.youthtalentfund.repositories.Projection.StaffDetailProjection;
import com.graduation.youthtalentfund.repositories.Projection.StaffProjection;
import com.graduation.youthtalentfund.repositories.UserRepository;
import com.graduation.youthtalentfund.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/create-staff")
    public ResponseEntity<UserInfoDTO> createStaff(@Valid @RequestBody CreateStaffRequest request, Authentication authentication) {
        String email = authentication.getName();
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        return ResponseEntity.ok(userService.createStaff(request, admin));
    }

    @GetMapping("/staffs")
    public ResponseEntity<Page<StaffProjection>> getOrSearchStaffs(@RequestParam String keyword, @PageableDefault Pageable pageable) {
        Page<StaffProjection> page = userService.getStaffs(keyword, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/staff")
    public ResponseEntity<UserInfoDTO> getStaffDetail(@RequestParam String value) {
        UserInfoDTO userInfoDTO = userService.getUserInfo(value);
        return ResponseEntity.ok(userInfoDTO);
    }

    @PutMapping("/staff")
    public ResponseEntity<UserInfoDTO> updateStaffProfile(@Valid @RequestBody UpdateProfileDTO profileUpdateDTO, @RequestParam String email) {
        UserInfoDTO userInfoDTO = userService.updateProfile(profileUpdateDTO, email);
        return ResponseEntity.ok(userInfoDTO);
    }

    @PostMapping("/staff/lock/{email}")
    public void lockUser(@PathVariable String email) {
        userService.lockUser(email);
    }

    @PostMapping("/staff/unlock/{email}")
    public void unlockUser(@PathVariable String email) {
        userService.unlockUser(email);
    }

    @DeleteMapping("/staff/{email}")
    public void deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
    }
}
