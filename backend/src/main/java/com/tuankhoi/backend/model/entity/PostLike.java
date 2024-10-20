package com.tuankhoi.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "post_like", indexes = {
        @Index(name = "idx_post_like_post_id", columnList = "post_id"),
        @Index(name = "idx_post_like_user_id", columnList = "user_id")
})
@EntityListeners(AuditingEntityListener.class)
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @CreatedDate
    @Column(updatable = false)
    LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    Post post;
}
