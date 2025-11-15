package com.graduation.youthtalentfund.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Lớp xử lý exception tập trung cho toàn bộ ứng dụng.
 * Bắt các exception được ném ra từ các Controller và chuyển đổi chúng
 * thành một HTTP response có cấu trúc và ý nghĩa.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // === 1. XỬ LÝ CÁC LỖI NGHIỆP VỤ CỤ THỂ ===
    // Các lỗi này do chúng ta chủ động ném ra từ tầng Service.

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND); // 404
    }

    @ExceptionHandler(DataConflictException.class)
    public ResponseEntity<ErrorDetails> handleDataConflictException(DataConflictException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT); // 409
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDetails> handleBadRequestException(BadRequestException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST); // 400
    }

    // === 2. XỬ LÝ CÁC LỖI CHUNG CỦA FRAMEWORK ===

    /**
     * Bắt lỗi khi validation (@Valid) trên các DTO request thất bại.
     * Trả về một JSON chứa lỗi của từng trường.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST); // 400
    }

    /**
     * Bắt lỗi khi đăng nhập sai (sai email hoặc mật khẩu).
     * Spring Security sẽ ném ra BadCredentialsException.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED); // 41
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ErrorDetails> handleFileUploadException(FileUploadException ex,WebRequest request){
        ErrorDetails errorDetails = new ErrorDetails(new Date(),ex.getMessage(),request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // === 3. "LƯỚI AN TOÀN CUỐI CÙNG" CHO CÁC LỖI HỆ THỐNG ===

    /**
     * Bắt tất cả các exception khác không được xử lý cụ thể ở trên.
     * Đây là "lưới an toàn" để tránh lộ các thông tin nhạy cảm của hệ thống.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        // Ghi log chi tiết của lỗi hệ thống để đội ngũ dev có thể điều tra
        logger.error("Đã xảy ra lỗi không mong muốn: ", ex);

        // Trả về một thông báo lỗi chung chung, an toàn cho client
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }
}