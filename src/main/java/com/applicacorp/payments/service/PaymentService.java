package com.applicacorp.payments.service;

import com.applicacorp.payments.dto.PaymentRequest;
import com.applicacorp.payments.dto.PaymentResponse;
import com.applicacorp.payments.entity.Payment;
import com.applicacorp.payments.entity.PaymentStatus;
import com.applicacorp.payments.exception.DuplicatePaymentException;
import com.applicacorp.payments.integration.core.CorePaymentSender;
import com.applicacorp.payments.mapper.PaymentXmlMapper;
import com.applicacorp.payments.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentXmlMapper paymentXmlMapper;
    private final CorePaymentSender corePaymentSender;

    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentXmlMapper paymentXmlMapper,
            CorePaymentSender corePaymentSender
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentXmlMapper = paymentXmlMapper;
        this.corePaymentSender = corePaymentSender;
    }

    public PaymentResponse processPayment(PaymentRequest request) {

        if (paymentRepository.existsById(request.id())) {
            throw new DuplicatePaymentException(request.id());
        }

        Payment payment = new Payment(
                request.id(),
                request.customerId(),
                request.amount(),
                request.currency(),
                request.timestamp(),
                PaymentStatus.PROCESSED
        );

        String xml = paymentXmlMapper.toXml(payment);

        corePaymentSender.send(payment.getId(), xml);

        Payment savedPayment = paymentRepository.save(payment);

        return toResponse(savedPayment);
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getCustomerId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getTimestamp(),
                payment.getStatus().name()
        );
    }

    public List<PaymentResponse> findPayments(
            String customerId,
            OffsetDateTime from,
            OffsetDateTime to
    ) {

        List<Payment> payments;

        if (customerId != null && from != null && to != null) {
            payments = paymentRepository
                    .findByCustomerIdAndTimestampBetween(
                            customerId,
                            from,
                            to
                    );

        } else if (customerId != null) {
            payments = paymentRepository.findByCustomerId(customerId);

        } else if (from != null && to != null) {
            payments = paymentRepository.findByTimestampBetween(from, to);

        } else {
            payments = paymentRepository.findAll();
        }

        return payments.stream()
                .map(this::toResponse)
                .toList();
    }
}
