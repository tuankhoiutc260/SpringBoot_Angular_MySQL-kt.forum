package com.tuankhoi.backend.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshRequest {
    private String token;
}
