package com.graduation.youthtalentfund.repositories;

import com.graduation.youthtalentfund.entities.Attachment;
import com.graduation.youthtalentfund.enums.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByOwnerIdAndOwnerType(Long ownerId, OwnerType ownerType);
}
