package com.example.notifyks.repo;

import com.example.notifyks.domain.EventEntity;
import com.example.notifyks.domain.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, String> {
    List<NotificationEntity> findByEventId(String eventId);
}
