package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.response.NotificationResponse;
import com.tuankhoi.backend.model.entity.User;
import com.tuankhoi.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(Authentication auth, @RequestParam(required = false) String originType) {
        User user = (User) auth.getPrincipal();
        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(user, originType);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/all")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(Authentication auth, @RequestParam(required = false) String originType) {
        User user = (User) auth.getPrincipal();
        List<NotificationResponse> notifications = notificationService.getAllNotifications(user, originType);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
