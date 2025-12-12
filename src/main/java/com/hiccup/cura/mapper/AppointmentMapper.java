package com.hiccup.cura.mapper;

import com.hiccup.cura.model.Appointment;
import com.hiccup.cura.request.AppointmentDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentMapper {
    private final ModelMapper mapper;

    public AppointmentDto appointmentToDto(Appointment appointment){
        return mapper.map(appointment, AppointmentDto.class);
    }

    public Appointment dtoToAppointment(AppointmentDto appointmentDto){
        return mapper.map(appointmentDto, Appointment.class);
    }
}
