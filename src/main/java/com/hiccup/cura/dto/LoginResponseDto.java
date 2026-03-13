package com.hiccup.cura.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private String jwtToken;
    private Long id;
}
