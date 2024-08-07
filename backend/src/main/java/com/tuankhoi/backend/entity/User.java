package com.tuankhoi.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    String image;

    boolean active = true;

    @CreatedBy
    @Column(updatable = false)
    String createdBy;

    @CreatedDate
    @Column(updatable = false)
    LocalDateTime createdDate;

    @LastModifiedBy
    String lastModifiedBy;

    @LastModifiedDate
    LocalDateTime lastModifiedDate;

    @ManyToOne
    @JoinColumn(name = "role_id")
    Role role;
}