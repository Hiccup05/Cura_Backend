package com.hiccup.cura.dto.response;

import com.hiccup.cura.enums.ReceptionistStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceptionistResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private ReceptionistStatus status;
    private String profilePictureUrl;
}
