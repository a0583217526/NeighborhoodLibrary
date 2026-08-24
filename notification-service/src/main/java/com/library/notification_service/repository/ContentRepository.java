package com.library.notification_service.repository;

import com.library.notification_service.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Integer> {
    Content findContentById(Integer id);
}
