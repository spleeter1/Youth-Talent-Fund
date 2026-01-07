package com.graduation.youthtalentfund.controllers;

import com.graduation.youthtalentfund.services.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {

    private final MailService mailService;

    @PostMapping("/public/test-mail")
    public ResponseEntity<?> testMail() {
        mailService.sendMail("ngdnam03@gmail.com", "Testmail", "Gửi mail");
        return ResponseEntity.ok("success");
    }
}
