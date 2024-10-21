package com.tuankhoi.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "sub_category")
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String title;

    @Column
    String description;

    @Column
    String imageUrl;

    @Column
    String cloudinaryImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    Category category;

    @OneToMany(mappedBy = "subCategory", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    List<Post> posts = new ArrayList<>();

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @ToString.Exclude
    User createdBy;

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
}