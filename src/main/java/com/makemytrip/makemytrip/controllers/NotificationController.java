package com.makemytrip.makemytrip.controllers;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.makemytrip.makemytrip.models.Notification;
import com.makemytrip.makemytrip.models.NotificationDto;
import com.makemytrip.makemytrip.models.NotifyData;
import com.makemytrip.makemytrip.repositories.NotificationRepository;

@RestController
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @PostMapping("/notify")
    public Notification notifyUser(@RequestBody NotificationDto notificationDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ADMIN"));
        if (isAdmin) {
            Notification notification = notificationRepository.findByEntityId(notificationDto.getEntityId());
            if (notification != null) {
                NotifyData notifyData = new NotifyData();
                notifyData.setMessage(notificationDto.getMessages().getMessage());
                notifyData.setUpdatedDate(notificationDto.getMessages().getUpdatedDate());
                List<NotifyData> notifyDatas = notification.getMessages();
                notifyDatas.add(notifyData);
                notification.setMessages(notifyDatas);
                notificationRepository.save(notification);
                return notification;
            }else{
                Notification newNotification = new Notification();
                NotifyData notifyData = new NotifyData();
                notifyData.setMessage(notificationDto.getMessages().getMessage());
                notifyData.setUpdatedDate(notificationDto.getMessages().getUpdatedDate());
                newNotification.setEntityId(notificationDto.getEntityId());
                newNotification.setMessages(List.of(notifyData));
                return notificationRepository.save(newNotification);
            }
        } else {
            throw new AuthorizationDeniedException("youu can't able to do this");
        }
    }

    @GetMapping("/notifications/{entityId}")
    public ResponseEntity<?> getNotifications(@PathVariable("entityId") String entityId) {
        SecurityContextHolder.getContext().getAuthentication();
        Notification notification = notificationRepository.findByEntityId(entityId);
        if (notification == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(notification);
    }

}
