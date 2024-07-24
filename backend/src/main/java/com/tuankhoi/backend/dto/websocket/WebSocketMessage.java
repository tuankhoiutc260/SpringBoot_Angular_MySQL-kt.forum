package com.tuankhoi.backend.dto.websocket;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebSocketMessage {
    String type;

    Object payload;
}