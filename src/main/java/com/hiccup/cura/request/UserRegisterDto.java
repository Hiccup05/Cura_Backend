package com.hiccup.cura.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDto {
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String password;
    private String mobNo;
}
