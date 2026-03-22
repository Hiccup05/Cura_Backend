package com.hiccup.cura.dto.response;

import com.hiccup.cura.enums.BloodGroup;
import com.nimbusds.openid.connect.sdk.claims.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientResponseDto {
    private Long id;
    private String firstName;
    private String lastName;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String phoneNumber;

    private String address;

    private BloodGroup bloodGroup;

    private String allergies;

    private String chronicConditions;

    private String emergencyContactName;

    private String emergencyContactPhone;
}
