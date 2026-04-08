package com.hiccup.cura.service.admin;

import com.hiccup.cura.dto.response.AdminProfileDto;
import com.hiccup.cura.dto.response.AdminStatsResponseDto;
import com.hiccup.cura.enums.DoctorStatus;
import com.hiccup.cura.enums.PaymentStatus;
import com.hiccup.cura.exception.custom.ResourceNotFoundException;
import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final ReceptionistRepository receptionistRepository;
    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;

    public AdminProfileDto getAdminProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id " + userId));
        return AdminProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public AdminStatsResponseDto getStats() {
        return AdminStatsResponseDto.builder()
                .totalDoctors(doctorRepository.countByDoctorStatusNot(DoctorStatus.INACTIVE))
                .totalPatients(patientRepository.count())
                .totalAppointments(appointmentRepository.count())
                .totalRevenue(paymentRepository.sumAmountByPaymentStatus(PaymentStatus.COMPLETE))
                .totalReceptionist(receptionistRepository.count())
                .build();
    }
}
