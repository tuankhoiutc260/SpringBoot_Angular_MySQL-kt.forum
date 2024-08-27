package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.websocket.WebSocketMessage;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Tag(name = "WebSocket Controller")
public class WebSocketController {
    @MessageMapping("/comment")
    @SendTo("/topic/comments")
    public WebSocketMessage handleComment(WebSocketMessage message) {
        return message;
    }
}
