package com.hiccup.cura.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminProfileDto {
    private Long id;
    private String username;
    private String email;
    private String profilePictureUrl;
}
