package com.hiccup.cura.service;

import com.hiccup.cura.specification.AppointmentSpecification;
import com.hiccup.cura.dto.reqeust.AppointmentRequestDto;
import com.hiccup.cura.dto.response.AppointmentResponseDto;
import com.hiccup.cura.dto.response.AppointmentSummaryDto;
import com.hiccup.cura.enums.*;
import com.hiccup.cura.exception.custom.*;
import com.hiccup.cura.mapper.AppointmentMapper;
import com.hiccup.cura.model.*;
import com.hiccup.cura.repository.*;
import com.hiccup.cura.service.doctor.DoctorScheduleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final ReceptionistRepository receptionistRepository;
    private final DoctorLeaveRepository doctorLeaveRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final EmailService emailService;
    private final DoctorScheduleService doctorScheduleService;
    private final MedicalServiceRepository medicalServiceRepository;
    private final AppointmentMapper appointmentMapper;
    private final Clock clock;

    @Transactional
    public AppointmentResponseDto createAppointment(AppointmentRequestDto appointmentRequestDto, Long userId) {
        //create new appointment object
        Appointment appointment=new Appointment();

        //fetch the doctor
        DoctorProfile doctor = getDoctor(appointmentRequestDto.getDoctorId());

        //fetch medical service
        MedicalService medicalService = getMedicalService(appointmentRequestDto.getMedicalServiceId());

        checkMedicalServiceMatch(appointment, doctor, medicalService);

        //check if the day selected falls on the doctor schedule
        DayOfWeek dayOfWeek = appointmentRequestDto.getAppointmentDate().getDayOfWeek();

        DoctorSchedule doctorSchedule=doctorScheduleService.getScheduleFromDay(doctor, dayOfWeek);

        checkDoctorAvailability(doctorSchedule, doctor, appointmentRequestDto);

        validateAppointmentTime(appointmentRequestDto.getAppointmentDate(), appointmentRequestDto.getAppointmentTime());

        validateSlot(doctorSchedule, medicalService, appointmentRequestDto.getAppointmentTime());

        if(appointmentRepository.existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatusNot( doctor, appointmentRequestDto.getAppointmentDate(), appointmentRequestDto.getAppointmentTime(), AppointmentStatus.CANCELLED)){
            throw new IllegalStateException("This appointment slot is already booked.");
        }

        checkBookCapacity(doctor, appointmentRequestDto, doctorSchedule);

        User user= getUser(userId);

        applyUserContext(appointment, user, userId, appointmentRequestDto);

        finalizeAppointment(appointment, doctor, appointmentRequestDto);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        emailService.sendAppointmentEmail(user.getEmail(), savedAppointment);

        return appointmentMapper.toDto(savedAppointment);
    }

    public AppointmentResponseDto getAppointment(Long userId, Long appointmentId){
        Appointment appointment = appointmentRepository.getAppointmentByIdAndUserId(appointmentId, userId).orElseThrow(() -> new ResourceNotFoundException("Appointment with id " + appointmentId + " not found"));
        return appointmentMapper.toDto(appointment);
    }

    public Page<AppointmentSummaryDto> getMyAppointments(Long userId, Pageable pageable){
        Page<Appointment> appointmentOfUser = appointmentRepository.getAppointmentOfUser(userId, pageable);
        return appointmentOfUser.map(appointmentMapper::toSummaryDto);
    }

    public Page<AppointmentSummaryDto> getReceptionistBookedAppointments(
            Long receptionistId,
            String walkInPatientName,
            Pageable pageable
    ) {
       Specification<Appointment> sp= AppointmentSpecification.hasReceptionistId(receptionistId).and(AppointmentSpecification.hasWalkInPatientName(walkInPatientName));
       Page<Appointment> appointments = appointmentRepository.findAll(sp, pageable);
       return appointments.map(appointmentMapper::toSummaryDto);
    }

    public AppointmentResponseDto getReceptionistAppointmentById(Long appointmentId) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment with id " + appointmentId + " not found"));

        if (appointment.getType() != AppointmentType.RECEPTIONIST_BOOKED) {
            throw new UnauthorizedUserAccessException("You are not allowed to access this appointment");
        }

        return appointmentMapper.toDto(appointment);
    }

    //return page of Appointment summary
    public Page<AppointmentSummaryDto> getDoctorAppointmentsFiltered(
            Long userId,
            String patientName,
            String walkInPatientName,
            String receptionistName,
            AppointmentStatus status,
            String dateFrom,
            String dateTo,
            Pageable pageable
    ) {

        DoctorProfile doctor = getDoctor(userId);

        Specification<Appointment> spec=AppointmentSpecification.hasDoctor(doctor).and(AppointmentSpecification.hasPatientName(patientName))
                .and(AppointmentSpecification.hasWalkInPatientName(walkInPatientName))
                .and(AppointmentSpecification.hasReceptionistName(receptionistName))
                .and(AppointmentSpecification.hasStatus(status))
                .and(AppointmentSpecification.hasDateFrom(dateFrom))
                .and(AppointmentSpecification.hasDateTo(dateTo));

        Page<Appointment> page = appointmentRepository.findAll(spec, pageable);
        return page.map(appointmentMapper::toSummaryDto);
    }

    public AppointmentResponseDto getDoctorAppointment(Long userId, Long appointmentId) {
        DoctorProfile doctor = getDoctor(userId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id " + appointmentId));

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new UnauthorizedUserAccessException("You are not allowed to access this appointment");
        }

        return appointmentMapper.toDto(appointment);
    }

    @Transactional
    public AppointmentResponseDto cancelAppointment(Long userId, Long appointmentId){
        User user=getUser(userId);
        if(user.getRole().stream().anyMatch(role -> role.getName().equals(RoleType.ADMIN)
                ||  role.getName().equals(RoleType.DOCTOR)
        )){
            throw new UnauthorizedUserAccessException("You are not allowed to access appointments");
        }
        Appointment appointment = appointmentRepository.getAppointmentByIdAndUserId(appointmentId, userId).orElseThrow(() -> new ResourceNotFoundException("Appointment with id " + appointmentId + " not found"));

        if(appointment.getStatus()==AppointmentStatus.CANCELLED){
            throw new InvalidAppointmentException("Appointment is already cancelled");
        }

        LocalDateTime appointmentTime=LocalDateTime.of(appointment.getAppointmentDate(), appointment.getAppointmentTime());
        Instant appointmentInstant=appointmentTime.atZone(clock.getZone()).toInstant();

        LocalDateTime cancelTime=LocalDateTime.now(clock);

        if(ChronoUnit.HOURS.between(appointment.getBookedAt(),appointmentInstant)<=24 ){
            if(cancelTime.isBefore(appointmentTime)){
                appointment.setStatus(AppointmentStatus.CANCELLED);
            }
            else{
                throw new CancellationNotAllowedException("Cannot cancel appointment as the cancel time exceeds appointment time");
            }
        }
        else{
            if(ChronoUnit.HOURS.between(appointment.getBookedAt(), clock.instant())<=24){
                appointment.setStatus(AppointmentStatus.CANCELLED);
            }else{
                throw new CancellationNotAllowedException("Cannot cancel appointment as booked appointment exceeds 24 hours mark");
            }
        }
        if (appointment.getPrescription() != null) {
            prescriptionRepository.delete(appointment.getPrescription());
            appointment.setPrescription(null);
        }
        emailService.sendCancellationEmail(user.getEmail(), appointment);
        return appointmentMapper.toDto(appointmentRepository.save(appointment));
    }

    private void validateAppointmentTime(LocalDate appointmentDate, LocalTime appointmentTime){
        LocalDateTime appointment=LocalDateTime.of(appointmentDate, appointmentTime);
        if(LocalDateTime.now(clock).isAfter(appointment)){
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
        ReceptionistProfile receptionist = receptionistRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found with id " + userId));

        if(receptionist.getStatus() == ReceptionistStatus.INACTIVE){
            throw new UnauthorizedUserAccessException("Current staff is inactive and cannot book appointment currently");
        }


        if (dto.getWalkInPatientName() == null || dto.getWalkInPatientPhone() == null) {
            throw new IllegalStateException("Walk-in patient name and phone are required.");
        }
        if (dto.getPaymentMethod() == null) {
            throw new IllegalStateException("Payment method is required for walk-in appointments.");
        }

        appointment.setReceptionist(receptionist);
        appointment.setType(AppointmentType.RECEPTIONIST_BOOKED);
        appointment.setPaymentMethod(dto.getPaymentMethod());
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setWalkInPatientName(dto.getWalkInPatientName());
        appointment.setWalkInPatientPhone(dto.getWalkInPatientPhone());
        appointment.setIsPaid(true);
    }

    private void checkMedicalServiceMatch(Appointment appointment, DoctorProfile doctor, MedicalService medicalService){
        boolean specializationMatch = doctor.getSpecialization().stream().anyMatch(spec ->
                spec.getId().equals(medicalService.getSpecialization().getId())
        );
        if (!specializationMatch) {
            throw new IllegalStateException("Doctor does not have the required specialization for this service");
        }
        appointment.setMedicalService(medicalService);
    }

    private void checkDoctorAvailability(DoctorSchedule doctorSchedule, DoctorProfile doctor, AppointmentRequestDto appointmentRequestDto){
        if(!Boolean.TRUE.equals(doctorSchedule.getIsAvailable())) {
            throw new IllegalStateException("Doctor is not available for this appointment day");
        }

        if(doctorLeaveRepository.isOnLeave(doctor.getId(), appointmentRequestDto.getAppointmentDate())){
            throw new IllegalStateException("Doctor is on the leave on "+appointmentRequestDto.getAppointmentDate());
        }
    }

    private void checkBookCapacity(DoctorProfile doctor, AppointmentRequestDto appointmentRequestDto, DoctorSchedule doctorSchedule){
        long bookedCount=appointmentRepository.countByDoctorAndAppointmentDate(doctor, appointmentRequestDto.getAppointmentDate());
        if(bookedCount>=doctorSchedule.getMaxAppointments()){
            throw new IllegalStateException("Doctor has reached maximum appointments for this day.");
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User cannot be not found with id " + userId));
    }

    private DoctorProfile getDoctor(Long doctorId){
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with user id " + doctorId));
    }

    public MedicalService getMedicalService(Long id){
        return medicalServiceRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Service isn't created with id " + id));

    }

    private void finalizeAppointment(Appointment appointment, DoctorProfile doctor, AppointmentRequestDto appointmentRequestDto){
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(appointmentRequestDto.getAppointmentDate());
        appointment.setAppointmentTime(appointmentRequestDto.getAppointmentTime());
        appointment.setBookedAt(clock.instant());
        appointment.setReason(appointmentRequestDto.getReason());
        appointment.setStatus(AppointmentStatus.PENDING);
        Prescription prescription=new Prescription();
        prescription.setAppointment(appointment);
        prescription=prescriptionRepository.save(prescription);
        appointment.setPrescription(prescription);
    }

}
