package com.tuankhoi.backend.dto.document;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import jakarta.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "sub_category")
public class SubCategoryDocument {
    @Id
    private String id;
    private String title;
    private String description;
    private String coverImage;
    private String categoryId;
}