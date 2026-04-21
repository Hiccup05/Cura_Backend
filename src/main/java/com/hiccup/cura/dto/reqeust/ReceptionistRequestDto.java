package com.hiccup.cura.dto.reqeust;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceptionistRequestDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
