    package com.tuankhoi.backend.dto.response;

    import lombok.*;
    import lombok.experimental.FieldDefaults;

    import java.time.LocalDateTime;
    import java.util.List;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public class SubCategoryResponse {
        String id;

        String title;

        String description;

        String coverImage;

//        List<PostResponse> posts;

        String createdBy;

        LocalDateTime createdDate;

        String lastModifiedBy;

        LocalDateTime lastModifiedDate;

        Integer totalPosts;
    }