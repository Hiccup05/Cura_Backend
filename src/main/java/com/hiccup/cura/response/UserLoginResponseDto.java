package com.hiccup.cura.response;

import jakarta.persistence.Column;

import java.util.Date;

public class UserLoginResponseDto {
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String mobNo;
    private Date dateOfBirth;
    private String address;
}
