package com.tuankhoi.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "test")
@EntityListeners(AuditingEntityListener.class)
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String title;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private String testImage;
}
