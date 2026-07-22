package com.hiccup.cura.dto.request;

import com.hiccup.cura.enums.ReceptionistStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeReceptionistRequestDto {
    private ReceptionistStatus status;
}
