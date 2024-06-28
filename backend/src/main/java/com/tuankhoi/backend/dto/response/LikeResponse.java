package com.tuankhoi.backend.dto.response;

import com.tuankhoi.backend.model.Post;
import com.tuankhoi.backend.model.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LikeResponse {
    LocalDateTime createdDate;

    User user;

    Post post;
}