package com.graduation.youthtalentfund.services.impl;

import com.graduation.youthtalentfund.constants.MessageConstants;
import com.graduation.youthtalentfund.dtos.request.LoginDTO;
import com.graduation.youthtalentfund.dtos.request.RegisterDTO;
import com.graduation.youthtalentfund.dtos.response.AuthResponseDTO;
import com.graduation.youthtalentfund.entities.Role;
import com.graduation.youthtalentfund.entities.User;
import com.graduation.youthtalentfund.entities.UserRole;
import com.graduation.youthtalentfund.enums.UserStatus;
import com.graduation.youthtalentfund.exceptions.DataConflictException;
import com.graduation.youthtalentfund.exceptions.ResourceNotFoundException;
import com.graduation.youthtalentfund.repositories.RoleRepository;
import com.graduation.youthtalentfund.repositories.UserRepository;
import com.graduation.youthtalentfund.security.JwtTokenProvider;
import com.graduation.youthtalentfund.services.AuthService;
import com.graduation.youthtalentfund.utils.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) {
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new DataConflictException(String.format(MessageConstants.EMAIL_IN_USE, registerDTO.getEmail()));
        }

        User user = new User();
        user.setFullName(registerDTO.getFullName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        user.setCode(CodeGenerator.generateUserCode());

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException(   String.format(MessageConstants.ROLE_NOT_FOUND_SYSTEM, "ROLE_USER")
                ));

        UserRole newUserRole = new UserRole();
        newUserRole.setUser(user);
        newUserRole.setRole(userRole);

        user.getUserRoles().add(newUserRole);

        userRepository.save(user);
    }

    @Override
    public AuthResponseDTO login(LoginDTO loginDTO) {
        // 1. Tạo đối tượng xác thực với email và mật khẩu thô
        // 2. Giao cho AuthenticationManager xử lý
        // Nó sẽ tự động gọi UserDetailsService, băm mật khẩu và so sánh
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );

        // 3. Nếu xác thực thành công, lưu thông tin vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 4. Tạo JWT token từ đối tượng Authentication đã được xác thực
        String jwt = jwtTokenProvider.generateToken(authentication);

        return new AuthResponseDTO(jwt);
    }
}