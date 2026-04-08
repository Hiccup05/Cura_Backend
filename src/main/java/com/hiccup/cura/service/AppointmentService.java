package com.hiccup.cura.service;

import com.hiccup.cura.dto.reqeust.AppointmentRequestDto;
import com.hiccup.cura.dto.response.AppointmentResponseDto;
import com.hiccup.cura.dto.response.AppointmentSummaryDto;
import com.hiccup.cura.dto.response.PrescriptionResponseDto;
import com.hiccup.cura.enums.AppointmentStatus;
import com.hiccup.cura.enums.AppointmentType;
import com.hiccup.cura.enums.PaymentMethod;
import com.hiccup.cura.enums.RoleType;
import com.hiccup.cura.exception.custom.CancellationNotAllowedException;
import com.hiccup.cura.exception.custom.InvalidBookingTimeException;
import com.hiccup.cura.exception.custom.ResourceNotFoundException;
import com.hiccup.cura.exception.custom.UnauthorizedUserAccessException;
import com.hiccup.cura.model.*;
import com.hiccup.cura.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final ReceptionistRepository receptionistRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final DoctorLeaveRepository doctorLeaveRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final EmailService emailService;

    @Transactional
    public AppointmentResponseDto createAppointment(AppointmentRequestDto appointmentRequestDto, Long userId) {
        Appointment appointment=new Appointment();
        DoctorProfile doctor = doctorRepository.findById(appointmentRequestDto.getDoctorId()).orElseThrow(() ->
                new ResourceNotFoundException("Doctor cannot be not found with id "+ appointmentRequestDto.getDoctorId()));
        MedicalService medicalService = medicalServiceRepository.findActiveMedicalServiceById(appointmentRequestDto.getMedicalServiceId()).orElseThrow(() ->
                new ResourceNotFoundException("Medical service cannot be not found with id " + appointmentRequestDto.getMedicalServiceId()));
        boolean specializationMatch = doctor.getSpecialization().stream().anyMatch(spec ->
                spec.getId().equals(medicalService.getSpecialization().getId())
        );
        if (!specializationMatch) {
            throw new IllegalStateException("Doctor does not have the required specialization for this service");
        }

        DayOfWeek dayOfWeek = appointmentRequestDto.getAppointmentDate().getDayOfWeek();
        DoctorSchedule doctorSchedule = doctorScheduleRepository.findByDayOfWeekAndDoctorProfile_Id(dayOfWeek, doctor.getId()).orElseThrow(() ->
                new ResourceNotFoundException("Doctor does not have the schedule for this appointment day")
        );

        if(!Boolean.TRUE.equals(doctorSchedule.getIsAvailable())) {
            throw new IllegalStateException("Doctor is not available for this appointment day");
        }

        if(doctorLeaveRepository.isOnLeave(doctor.getId(), appointmentRequestDto.getAppointmentDate())){
            throw new IllegalStateException("Doctor is on the leave on "+appointmentRequestDto.getAppointmentDate());
        }

        validateAppointmentTime(appointmentRequestDto.getAppointmentDate(), appointmentRequestDto.getAppointmentTime());

        validateSlot(doctorSchedule, medicalService, appointmentRequestDto.getAppointmentTime());

        if(appointmentRepository.existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatusNot( doctor, appointmentRequestDto.getAppointmentDate(), appointmentRequestDto.getAppointmentTime(), AppointmentStatus.CANCELLED)){
            throw new IllegalStateException("This appointment slot is already booked.");
        }

        long bookedCount=appointmentRepository.countByDoctorAndAppointmentDate(doctor, appointmentRequestDto.getAppointmentDate());
        if(bookedCount>=doctorSchedule.getMaxAppointments()){
            throw new IllegalStateException("Doctor has reached maximum appointments for this day.");
        }

        User user=userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User cannot be not found with id " + userId));
        applyUserContext(appointment, user, userId, appointmentRequestDto);

        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(appointmentRequestDto.getAppointmentDate());
        appointment.setAppointmentTime(appointmentRequestDto.getAppointmentTime());
        appointment.setBookedAt(LocalDateTime.now());
        appointment.setMedicalService(medicalService);
        appointment.setReason(appointmentRequestDto.getReason());
        Prescription prescription=new Prescription();
        prescription.setAppointment(appointment);
        prescription=prescriptionRepository.save(prescription);
        appointment.setPrescription(prescription);
        Appointment save = appointmentRepository.save(appointment);
        emailService.sendAppointmentEmail(user.getEmail(), save);
        return mapToDto(save);
    }

    public AppointmentResponseDto getAppointment(Long userId, Long appointmentId){
        User user=userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User cannot be not found with id " + userId));
        if(user.getRole().stream().anyMatch(role -> role.getName().equals(RoleType.ADMIN)
                ||  role.getName().equals(RoleType.DOCTOR)
        )){
            throw new UnauthorizedUserAccessException("You are not allowed to access appointments");
        }
        Appointment appointment = appointmentRepository.getAppointmentByIdAndUserId(appointmentId, userId).orElseThrow(() -> new ResourceNotFoundException("Appointment with id " + appointmentId + " not found"));
        return mapToDto(appointment);
    }

    public List<AppointmentSummaryDto> getMyAppointments(Long userId){
        List<Appointment> appointmentOfUser = appointmentRepository.getAppointmentOfUser(userId);
        return appointmentOfUser.stream().map(this::mapToSummaryDto).toList();
    }

    public List<AppointmentSummaryDto> getReceptionistBookedAppointments(
            Long userId,
            Long receptionistId,
            String walkInPatientName
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User cannot be not found with id " + userId));

        if (user.getRole().stream().noneMatch(role -> role.getName().equals(RoleType.RECEPTIONIST))) {
            throw new UnauthorizedUserAccessException("You are not allowed to access appointments");
        }

        List<Appointment> appointments =
                appointmentRepository.findReceptionistBookedAppointments(receptionistId, walkInPatientName);

        return appointments.stream().map(this::mapToSummaryDto).toList();
    }

    public AppointmentResponseDto getReceptionistAppointmentById(Long userId, Long appointmentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User cannot be not found with id " + userId));

        if (user.getRole().stream().noneMatch(role -> role.getName().equals(RoleType.RECEPTIONIST))) {
            throw new UnauthorizedUserAccessException("You are not allowed to access appointments");
        }

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment with id " + appointmentId + " not found"));

        if (appointment.getType() != AppointmentType.RECEPTIONIST_BOOKED) {
            throw new UnauthorizedUserAccessException("You are not allowed to access this appointment");
        }

        return mapToDto(appointment);
    }

    public Page<AppointmentSummaryDto> getDoctorAppointmentsFiltered(
            Long doctorUserId,
            String patientName,
            String walkInPatientName,
            String receptionistName,
            AppointmentStatus status,
            String dateFrom,
            String dateTo,
            Pageable pageable
    ) {
        LocalDate from;
        LocalDate to;
        if (dateFrom != null && !dateFrom.isBlank()) {
            from = parseDate(dateFrom, "date from");
        } else {
            from = null;
        }
        if (dateTo != null && !dateTo.isBlank()) {
            to = parseDate(dateTo, "date to");
        } else {
            to = null;
        }
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("dateFrom cannot be after dateTo");
        }
        DoctorProfile doctor = doctorRepository.findById(doctorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor cannot be not found with id " + doctorUserId));
        Specification<Appointment> spec = Specification
                .where((root, query, cb) -> cb.equal(root.get("doctor"), doctor));
        if (patientName != null && !patientName.isBlank()) {
            String like = "%" + patientName.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> {
                var patient = root.join("patient", jakarta.persistence.criteria.JoinType.LEFT);
                var fullName = cb.concat(
                        cb.concat(cb.lower(patient.get("firstName")), " "),
                        cb.lower(patient.get("lastName"))
                );
                return cb.like(fullName, like);
            });
        }
        if (walkInPatientName != null && !walkInPatientName.isBlank()) {
            String like = "%" + walkInPatientName.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("walkInPatientName")), like));
        }
        if (receptionistName != null && !receptionistName.isBlank()) {
            String like = "%" + receptionistName.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> {
                var receptionist = root.join("receptionist", jakarta.persistence.criteria.JoinType.LEFT);
                var fullName = cb.concat(
                        cb.concat(cb.lower(receptionist.get("firstName")), " "),
                        cb.lower(receptionist.get("lastName"))
                );
                return cb.like(fullName, like);
            });
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (from != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("appointmentDate"), from));
        }
        if (to != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("appointmentDate"), to));
        }
        Page<Appointment> page = appointmentRepository.findAll(spec, pageable);
        return page.map(this::mapToSummaryDto);
    }

    @Transactional
    public AppointmentResponseDto cancelAppointment(Long userId, Long appointmentId){
        User user=userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User cannot be not found with id " + userId));
        if(user.getRole().stream().anyMatch(role -> role.getName().equals(RoleType.ADMIN)
                ||  role.getName().equals(RoleType.DOCTOR)
        )){
            throw new UnauthorizedUserAccessException("You are not allowed to access appointments");
        }
        Appointment appointment = appointmentRepository.getAppointmentByIdAndUserId(appointmentId, userId).orElseThrow(() -> new ResourceNotFoundException("Appointment with id " + appointmentId + " not found"));

        LocalDateTime appointmentTime=LocalDateTime.of(appointment.getAppointmentDate(), appointment.getAppointmentTime());

        LocalDateTime cancelTime=LocalDateTime.now();

        if(ChronoUnit.HOURS.between(appointment.getBookedAt(),appointmentTime)<=24 ){
            if(cancelTime.isBefore(appointmentTime)){
                appointment.setStatus(AppointmentStatus.CANCELLED);
            }
            else{
                throw new CancellationNotAllowedException("Cannot cancel appointment as the cancel time exceeds appointment time");
            }
        }
        else{
            if(ChronoUnit.HOURS.between(appointment.getBookedAt(), cancelTime)<=24){
                appointment.setStatus(AppointmentStatus.CANCELLED);
            }else{
                throw new CancellationNotAllowedException("Cannot cancel appointment as booked appointment exceeds 5 hours mark");
            }
        }
        prescriptionRepository.findById(appointment.getPrescription().getId()).ifPresent(prescriptionRepository::delete);
        appointment.setPrescription(null);
        emailService.sendCancellationEmail(user.getEmail(), appointment);
        return mapToDto(appointmentRepository.save(appointment));
    }

    private AppointmentResponseDto mapToDto(Appointment appointment) {
        return AppointmentResponseDto.builder()
                .id(appointment.getId())
                .doctorId(appointment.getDoctor().getId())
                .medicalServiceId(appointment.getMedicalService().getId())
                .medicalServiceName(appointment.getMedicalService().getName())
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus())
                .type(appointment.getType())
                .reason(appointment.getReason())
                .price(appointment.getMedicalService().getPrice())
                .durationMinutes(appointment.getMedicalService().getDurationMinutes())
                .isPaid(appointment.getIsPaid())
                .paymentMethod(appointment.getPaymentMethod())
                .bookedAt(appointment.getBookedAt())
                .patientId(appointment.getPatient() != null ? appointment.getPatient().getId() : null)
                .patientName(appointment.getPatient() != null ?
                        appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName() : null)
                .receptionistId(appointment.getReceptionist() != null ? appointment.getReceptionist().getId() : null)
                .receptionistName(appointment.getReceptionist() != null ?
                        appointment.getReceptionist().getFirstName() + " " + appointment.getReceptionist().getLastName() : null)
                .walkInPatientName(appointment.getWalkInPatientName())
                .walkInPatientPhone(appointment.getWalkInPatientPhone())
                .prescriptionResponseDto(appointment.getPrescription()!=null? mapToPrescriptionDto(appointment.getPrescription()):null)
                .build();
    }
    private void validateAppointmentTime(LocalDate appointmentDate, LocalTime appointmentTime){
        LocalDateTime appointment=LocalDateTime.of(appointmentDate, appointmentTime);
        if(LocalDateTime.now().isAfter(appointment)){
            throw new InvalidBookingTimeException("Appointment time is before booking time");
        }

    }
    private void validateSlot(DoctorSchedule schedule, MedicalService service, LocalTime requestTime) {
        LocalTime startTime = schedule.getStartTime();
        int duration = service.getDurationMinutes();
        long minutesDiff = ChronoUnit.MINUTES.between(startTime, requestTime);

        if (minutesDiff < 0 || minutesDiff % duration != 0) {
            throw new IllegalStateException("Invalid appointment slot. Please select valid time slot.");
        }
        if (requestTime.isAfter(schedule.getEndTime().minusMinutes(duration))) {
            throw new IllegalStateException("Appointment time is outside doctor's schedule");
        }
    }

    private void applyUserContext(Appointment appointment, User user, Long userId, AppointmentRequestDto dto) {
        if (user.getRole().stream().anyMatch(role -> role.getName().equals(RoleType.PATIENT))) {
            applyPatientContext(appointment, userId);
        } else if (user.getRole().stream().anyMatch(role -> role.getName().equals(RoleType.RECEPTIONIST))) {
            applyReceptionistContext(appointment, userId, dto);
        } else {
            throw new IllegalStateException("The user cannot book the appointment");
        }
    }

    private void applyPatientContext(Appointment appointment, Long userId) {
        PatientProfile patient = patientRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id " + userId));
        if (patient.getDateOfBirth() == null || patient.getPhoneNumber() == null ||
                patient.getGender() == null || patient.getBloodGroup() == null) {
            throw new IllegalStateException("Please complete your profile before booking an appointment");
        }
        appointment.setPatient(patient);
        appointment.setType(AppointmentType.PATIENT_BOOKED);
        appointment.setPaymentMethod(PaymentMethod.ONLINE);
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setIsPaid(false);
    }

    private void applyReceptionistContext(Appointment appointment, Long userId, AppointmentRequestDto dto) {
        if (dto.getWalkInPatientName() == null || dto.getWalkInPatientPhone() == null) {
            throw new IllegalStateException("Walk-in patient name and phone are required.");
        }
        if (dto.getPaymentMethod() == null) {
            throw new IllegalStateException("Payment method is required for walk-in appointments.");
        }
        ReceptionistProfile receptionist = receptionistRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found with id " + userId));
        appointment.setReceptionist(receptionist);
        appointment.setType(AppointmentType.RECEPTIONIST_BOOKED);
        appointment.setPaymentMethod(dto.getPaymentMethod());
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setWalkInPatientName(dto.getWalkInPatientName());
        appointment.setWalkInPatientPhone(dto.getWalkInPatientPhone());
        appointment.setIsPaid(true);
    }

    private AppointmentSummaryDto mapToSummaryDto(Appointment appointment) {
        return AppointmentSummaryDto.builder()
                .appointmentId(appointment != null ? appointment.getId() : null)
                .appointmentDate(appointment != null ? appointment.getAppointmentDate() : null)
                .appointmentTime(appointment != null ? appointment.getAppointmentTime() : null)
                .appointmentStatus(appointment != null ? appointment.getStatus() : null)
                .doctorId(
                        appointment != null && appointment.getDoctor() != null
                                ? appointment.getDoctor().getId()
                                : null
                )
                .medicalServiceName(
                        appointment != null && appointment.getMedicalService() != null
                                ? appointment.getMedicalService().getName()
                                : null
                )
                .isPaid(appointment != null ? appointment.getIsPaid() : null)
                .receptionistId(
                        appointment != null && appointment.getReceptionist() != null
                                ? appointment.getReceptionist().getId()
                                : null
                )
                .walkInPatientName(appointment != null ? appointment.getWalkInPatientName() : null)
                .build();
    }

    private PrescriptionResponseDto mapToPrescriptionDto(Prescription prescription){
        return new  PrescriptionResponseDto(prescription.getId(), prescription.getDescription());
    }

    private LocalDate parseDate(String value, String fieldName) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(fieldName + " must be in YYYY-MM-DD format");
        }
    }
}
