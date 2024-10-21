package com.tuankhoi.backend.dto.response;

import com.tuankhoi.backend.model.entity.Notification;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse implements Serializable {
    String id;

    String content;

    boolean isRead;

    LocalDateTime createdDate;

    String originType;

    String originId;

    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.content = notification.getContent();
        this.isRead = notification.isRead();
        this.createdDate = notification.getCreatedDate();
        this.originType = notification.getOriginType();
        this.originId = notification.getOriginId();
    }
}
