package com.hiccup.cura.dto.reqeust;

import com.hiccup.cura.enums.BloodGroup;
import com.hiccup.cura.model.User;
import com.nimbusds.openid.connect.sdk.claims.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientRequestDto {
    private Long id;
    private String firstName;
    private String lastName;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phoneNumber;
    private String address;

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    private String allergies;

    private String chronicConditions;

    private String emergencyContactName;

    private String emergencyContactPhone;
}
