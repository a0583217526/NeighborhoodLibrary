package com.library.notification_service.service;

import com.library.notification_service.entity.TypeAdditionalDetails;
import com.library.notification_service.repository.TypeAdditionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationTypeService {

    @Autowired
    private TypeAdditionalRepository repository;

    public List<TypeAdditionalDetails> getAllNotificationTypes() {
        return repository.findAll();
    }
}