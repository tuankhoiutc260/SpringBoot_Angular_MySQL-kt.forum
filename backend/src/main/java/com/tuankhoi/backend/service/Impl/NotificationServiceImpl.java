package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.response.NotificationResponse;
import com.tuankhoi.backend.model.entity.Notification;
import com.tuankhoi.backend.model.entity.User;
import com.tuankhoi.backend.repository.Jpa.NotificationRepository;
import com.tuankhoi.backend.service.NotificationService;
import com.tuankhoi.backend.websocket.UserWebSocketHandler;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {
    NotificationRepository notificationRepository;
    UserWebSocketHandler userWebSocketHandler;

    @Override
    public void createNotification(User user, String content, String originType, String originId) throws IOException {
        Notification notification = new Notification();

        notification.setUser(user);
        notification.setContent(content);
        notification.setRead(false);
        notification.setCreatedDate(LocalDateTime.now());
        notification.setOriginType(originType);
        notification.setOriginId(originId);

        notificationRepository.save(notification);

        if(userWebSocketHandler.isUserOnline(user)){
            NotificationResponse notificationResponse = new NotificationResponse(notification);
            userWebSocketHandler.sendNotificationToUser(user, notificationResponse);
        }
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications(User user, String originType) {
        List<Notification> notifications = notificationRepository.findByUserAndIsReadFalseAndOriginTypeOrderByCreatedDateDesc(user, originType);
        return notifications.stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getAllNotifications(User user, String originType) {
        List<Notification> notifications = notificationRepository.findByUserAndOriginTypeOrderByCreatedDateDesc(user, originType);
        return notifications.stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
