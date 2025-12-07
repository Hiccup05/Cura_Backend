package com.hiccup.cura.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor

public class UserLoginResponseDto {
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String mobNo;
    private Date dateOfBirth;
    private String address;
}
