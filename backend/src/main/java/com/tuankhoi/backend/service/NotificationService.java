package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.response.NotificationResponse;
import com.tuankhoi.backend.model.entity.User;

import java.io.IOException;
import java.util.List;

public interface NotificationService {
    void createNotification(User user, String content, String originType, String originId) throws IOException;

    List<NotificationResponse> getUnreadNotifications(User user, String originType);

    List<NotificationResponse> getAllNotifications(User user, String originType);

    void markAsRead(String notificationId);
}
