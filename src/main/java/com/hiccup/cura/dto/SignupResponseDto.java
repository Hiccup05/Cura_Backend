package com.hiccup.cura.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupResponseDto {
    private Long id;
    private String username;
}
