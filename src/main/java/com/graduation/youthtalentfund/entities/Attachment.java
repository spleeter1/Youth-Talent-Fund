package com.graduation.youthtalentfund.entities;

import com.graduation.youthtalentfund.enums.OwnerType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "attachments", indexes = {
        @Index(name = "idx_attachment_filepath", columnList = "filePath", unique = true),
        @Index(name = "idx_attachment_owner", columnList = "ownerId, ownerType")
})
public class Attachment extends BaseEntity {

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String filePath;

    @Column(length = 100)
    private String fileType;

    private Long fileSize;

    @Column(nullable = false)
    private Long ownerId;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private OwnerType ownerType;
}