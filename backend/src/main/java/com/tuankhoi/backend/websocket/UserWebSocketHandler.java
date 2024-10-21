package com.tuankhoi.backend.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuankhoi.backend.dto.response.NotificationResponse;
import com.tuankhoi.backend.model.entity.User;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> connectedUsers = new ConcurrentHashMap<>();

    public UserWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(@Nonnull WebSocketSession session) throws Exception {
        String userId = getUserId(session);
        connectedUsers.put(userId, session);
    }

    @Override
    public void afterConnectionClosed(@Nonnull WebSocketSession session, @Nonnull CloseStatus status) throws Exception {
        String userId = getUserId(session);
        connectedUsers.remove(userId);
    }

    private String getUserId(WebSocketSession session){
        Principal principal = session.getPrincipal();
        return principal != null ? principal.getName() : null;
    }

    public boolean isUserOnline(User user){
        return connectedUsers.containsKey(user.getId());
    }

    public void sendNotificationToUser(User user, NotificationResponse notificationResponse) throws IOException {
        WebSocketSession session = connectedUsers.get((user.getId()));
        if(session != null && session.isOpen()){
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(notificationResponse)));
        }
    }
}
