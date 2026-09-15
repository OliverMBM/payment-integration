package com.applicacorp.payments.repository;

import com.applicacorp.payments.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    List<Payment> findByCustomerId(String customerId);

    List<Payment> findByTimestampBetween(
            OffsetDateTime from,
            OffsetDateTime to
    );

    List<Payment> findByCustomerIdAndTimestampBetween(
            String customerId,
            OffsetDateTime from,
            OffsetDateTime to
    );
}