package com.makemytrip.makemytrip.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.makemytrip.makemytrip.models.Notification;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    Notification findByEntityId(String Id);
    void deleteByEntityId(String entityId);
}
