package com.tuankhoi.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private UUID id;
    private String title;
    private String content;
    private Set<String> tags = new HashSet<>();
    private LocalDateTime createdDate;
    private UUID createdBy;
    private LocalDateTime lastModifiedDate;
    private UUID lastModifiedBy;
}
