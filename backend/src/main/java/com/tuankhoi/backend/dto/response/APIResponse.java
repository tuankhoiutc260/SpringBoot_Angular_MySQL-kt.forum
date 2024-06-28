package com.tuankhoi.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class APIResponse <T>{
    @Builder.Default
    private int code = 1000;
    private String message;
    private T result;
}
