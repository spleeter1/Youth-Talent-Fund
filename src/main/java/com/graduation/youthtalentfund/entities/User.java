package com.graduation.youthtalentfund.entities;

import com.graduation.youthtalentfund.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(length = 50, nullable = false, unique = true)
    private String code;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Column
    private String phoneNumber;

    @Column
    private String address;

    @Column
    private String avatarPath;

    @Lob // Dành cho các trường văn bản dài
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private UserStatus status;

    @Column(nullable = false)
    private Integer tokenVersion = 1;

    /**
     * Mối quan hệ Một-Nhiều đến bảng nối UserRole.
     * 'mappedBy = "user"': Báo cho JPA biết rằng mối quan hệ này được quản lý
     * bởi trường 'user' trong lớp UserRole.
     * 'cascade = CascadeType.ALL': Các thao tác (lưu, xóa...) trên User sẽ được
     * tự động áp dụng cho các UserRole liên quan.
     * 'orphanRemoval = true': Nếu một UserRole bị xóa khỏi Set này, nó sẽ
     * tự động bị xóa khỏi CSDL.
     */
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY // Dùng LAZY để tối ưu, chỉ tải các role khi thực sự cần
    )
    private Set<UserRole> userRoles = new HashSet<>();

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_password_token_expiry")
    private LocalDateTime resetPasswordTokenExpiry;
}
