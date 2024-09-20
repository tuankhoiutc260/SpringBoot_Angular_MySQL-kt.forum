package com.tuankhoi.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "permission")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(unique = true, nullable = false)
    String name;

    String description;

    @ManyToMany(mappedBy = "permissions", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @ToString.Exclude
    Set<Role> roles;
}
