package com.hiccup.cura.dto.reqeust;

import com.hiccup.cura.enums.DoctorStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeStatusRequestDto {
    private DoctorStatus doctorStatus;
}
