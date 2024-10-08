package com.tuankhoi.backend.dto.document;

import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(indexName = "post")
public class PostDocument {
    @Id
    String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    String title;

    @Field(type = FieldType.Text, analyzer = "standard")
    String content;

    String subCategoryId;

    String createdBy;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    LocalDateTime createdDate;

    String lastModifiedBy;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    LocalDateTime lastModifiedDate;

    @Builder.Default
    int viewCount = 0;;

    int commentCount;
}
