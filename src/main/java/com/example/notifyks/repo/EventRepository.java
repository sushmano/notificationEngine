package com.example.notifyks.repo;

import com.example.notifyks.domain.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventEntity, String> {
}
