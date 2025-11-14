package com.graduation.youthtalentfund.controllers;

import com.graduation.youthtalentfund.constants.MessageConstants;
import com.graduation.youthtalentfund.dtos.request.ForgotPasswordDTO;
import com.graduation.youthtalentfund.dtos.request.ResetPasswordDTO;
import com.graduation.youthtalentfund.dtos.response.AuthResponseDTO;
import com.graduation.youthtalentfund.dtos.request.LoginDTO;
import com.graduation.youthtalentfund.dtos.request.RegisterDTO;
import com.graduation.youthtalentfund.services.AuthService;
import com.graduation.youthtalentfund.services.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

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

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
         passwordResetService.processForgotPassword(forgotPasswordDTO.getEmail());

        return ResponseEntity.ok("Nếu email của bạn tồn tại, một liên kết đặt lại mật khẩu đã được gửi.");
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> validateResetToken(@RequestParam("token") String token) {
         passwordResetService.validateResetToken(token);
        return ResponseEntity.ok("Token hợp lệ.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
         passwordResetService.processResetPassword(resetPasswordDTO.getToken(), resetPasswordDTO.getNewPassword());
        return ResponseEntity.ok("Mật khẩu của bạn đã được đặt lại thành công.");
    }

//    POST /api/auth/forgot-password: Người dùng gửi email.
//    Backend tạo token, lưu vào DB, và gửi email chứa link http://.../reset-password?token=abc-123.
//    Người dùng click link: Frontend nhận token từ URL, gọi GET /api/auth/reset-password?token=abc-123 để kiểm tra.
//    Nếu OK, Frontend hiển thị form nhập mật khẩu mới.
//    POST /api/auth/reset-password: Frontend gửi token và mật khẩu mới.
//    Backend xác thực token lần nữa, đổi mật khẩu và vô hiệu hóa token.
//    Người dùng có thể đăng nhập bằng mật khẩu mới.
}
