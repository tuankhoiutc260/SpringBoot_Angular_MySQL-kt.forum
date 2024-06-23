package com.tuankhoi.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse <T>{
    private int code = 1000;
    private String message;
    private T result;
}
