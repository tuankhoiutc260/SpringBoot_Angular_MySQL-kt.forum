package com.tuankhoi.backend.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post")
@EntityListeners(AuditingEntityListener.class)
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String title;

    @Column(columnDefinition = "TEXT")
    String content;

    @ElementCollection
    Set<String> tags = new HashSet<>();

    @CreatedDate
    @Column(updatable = false)
    LocalDateTime createdDate;

    @CreatedBy
    @Column(updatable = false)
    String createdBy;

    @LastModifiedDate
    LocalDateTime lastModifiedDate;

    @LastModifiedBy
    String lastModifiedBy;

//    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
//    Set<Like> likes;

//    private Set<Comment> comments;
}
