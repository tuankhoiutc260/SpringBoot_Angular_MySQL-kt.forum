package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.websocket.WebSocketMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/comment")
    @SendTo("/topic/comments")
    public WebSocketMessage handleComment(WebSocketMessage message) {
        // Xử lý logic nếu cần
        return message;
    }
}