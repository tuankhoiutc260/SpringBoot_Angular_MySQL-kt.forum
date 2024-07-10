package com.tuankhoi.backend.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

/**
 * For error codes, see {@link com.tuankhoi.backend.exception.ErrorCode}
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestRequest {
    String title;
    MultipartFile testImage;
}