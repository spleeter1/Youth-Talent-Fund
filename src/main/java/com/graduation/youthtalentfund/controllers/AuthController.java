package com.graduation.youthtalentfund.controllers;

import com.graduation.youthtalentfund.constants.MessageConstants;
import com.graduation.youthtalentfund.dtos.response.AuthResponseDTO;
import com.graduation.youthtalentfund.dtos.request.LoginDTO;
import com.graduation.youthtalentfund.dtos.request.RegisterDTO;
import com.graduation.youthtalentfund.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterDTO registerDTO) {
            authService.register(registerDTO);
            return new ResponseEntity<>(MessageConstants.USER_REGISTERED_SUCCESS, HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> authenticateUser(@Valid @RequestBody LoginDTO loginDTO) {
        AuthResponseDTO authResponse = authService.login(loginDTO);
        return ResponseEntity.ok(authResponse);
    }
}
