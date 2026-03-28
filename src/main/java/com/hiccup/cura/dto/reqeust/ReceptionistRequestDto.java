package com.hiccup.cura.dto.reqeust;

import com.hiccup.cura.enums.ReceptionistStatus;
import com.hiccup.cura.model.User;
import jakarta.persistence.*;
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
