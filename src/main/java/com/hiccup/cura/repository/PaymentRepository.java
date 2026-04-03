package com.hiccup.cura.repository;

import com.hiccup.cura.enums.PaymentStatus;
import com.hiccup.cura.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByAppointment_IdAndPaymentStatusNotIn(Long appointmentId, List<PaymentStatus> paymentStatus);

    Payment findByPidx(String pidx);
}
