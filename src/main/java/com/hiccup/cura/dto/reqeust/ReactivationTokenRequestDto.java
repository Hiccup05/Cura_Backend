package com.hiccup.cura.dto.reqeust;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactivationTokenRequestDto {
    private String email;
    private String token;
}
