package com.tuankhoi.backend.dto.response;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubCategoryResponse {
    String title;

    String description;

    String coverImage;

    List<PostResponse> posts;

    String createdBy;

    LocalDateTime createdDate;

    String lastModifiedBy;

    LocalDateTime lastModifiedDate;
}