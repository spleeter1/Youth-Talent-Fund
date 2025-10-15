package com.graduation.youthtalentfund.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
// Thêm định nghĩa ràng buộc UNIQUE ở đây
@Table(name = "user_roles", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "role_id"})
})
public class UserRole extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by_admin_id")
    private User assignedBy;
}
