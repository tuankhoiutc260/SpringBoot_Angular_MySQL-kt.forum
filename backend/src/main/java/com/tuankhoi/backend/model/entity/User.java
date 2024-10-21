package com.tuankhoi.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String userName;

    @Column(nullable = false)
    String password;

    @Column(unique = true, nullable = false)
    String email;

    String fullName;

    @Column
    String imageUrl;

    @Column
    String cloudinaryImageId;

    @Column
    String aboutMe;

    @Column
    @Builder.Default
    boolean active = true;

    @CreatedDate
    @Column(updatable = false)
    LocalDateTime createdDate;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_modified_by")
    @ToString.Exclude
    User lastModifiedBy;

    @LastModifiedDate
    LocalDateTime lastModifiedDate;

    @ManyToOne
    @JoinColumn(name = "role_id")
    Role role;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    List<PostLike> postLikes;
}