package com.tuankhoi.backend.dto;

import com.tuankhoi.backend.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
    private Integer id;
    private RoleEnum name;
    private String description;
}
