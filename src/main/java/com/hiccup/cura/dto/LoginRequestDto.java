package com.hiccup.cura.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class LoginRequestDto {
    private String username;
    private String password;
}
