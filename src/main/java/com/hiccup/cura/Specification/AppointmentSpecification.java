package com.hiccup.cura.Specification;

import com.hiccup.cura.enums.AppointmentStatus;
import com.hiccup.cura.enums.AppointmentType;
import com.hiccup.cura.model.Appointment;
import com.hiccup.cura.model.DoctorProfile;
import com.hiccup.cura.util.DateParser;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class AppointmentSpecification {
    public static Specification<Appointment> hasType(AppointmentType type){
        return ((root, query, cb) -> cb.equal(root.get("type"), type));
    }

    public static Specification<Appointment> hasReceptionistId(Long receptionistId){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("receptionist").get("id"), receptionistId));
    }

    public static Specification<Appointment> hasWalkInPatientName(String walkInPatientName){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("walkInPatientName")), "%" + walkInPatientName + "%"));
    }


    public static Specification<Appointment> hasDoctor(DoctorProfile doctor){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("doctor"), doctor));
    }

    public static Specification<Appointment> hasPatientName(String patientName){
        if(patientName==null || patientName.isBlank()) return null;
        String like= "%" + patientName.trim().toLowerCase()+ "%";
        return ((root, query, criteriaBuilder) -> {
            var patient =root.join("patient", JoinType.LEFT);
            var fullName= criteriaBuilder.concat(
                    criteriaBuilder.concat(criteriaBuilder.lower(patient.get("firstName")), " "),
                    criteriaBuilder.lower(patient.get("lastName"))
                    );
            return criteriaBuilder.like(fullName, like);
        });
    }

    public static Specification<Appointment> hasReceptionistName(String receptionistName){
        if(receptionistName==null || receptionistName.isBlank()) return null;
        String like="%" + receptionistName.trim().toLowerCase() + "%";
        return ((root, query, criteriaBuilder) -> {
            var receptionist=root.join("receptionist", JoinType.LEFT);
            var fullName=criteriaBuilder.concat(
                    criteriaBuilder.concat(criteriaBuilder.lower(receptionist.get("firstName")), " "),
                    criteriaBuilder.lower(receptionist.get("lastName")));
            return criteriaBuilder.like(fullName, like);
        });
    }

    public static Specification<Appointment> hasStatus(AppointmentStatus status){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status));
    }

    public static Specification<Appointment> hasDateFrom(String dateFrom){
        if(dateFrom==null || dateFrom.isBlank()) return null;
        LocalDate from = DateParser.parseDate(dateFrom, "date from");
        return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("appointmentDate"), from));
    }

    public static Specification<Appointment> hasDateTo(String dateTo){
        if(dateTo==null || dateTo.isBlank()) return null;
        LocalDate to = DateParser.parseDate(dateTo, "date to");
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("appointmentDate"), to));
    }

}
