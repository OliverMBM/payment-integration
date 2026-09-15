package com.applicacorp.payments.service;

import com.applicacorp.payments.dto.PaymentRequest;
import com.applicacorp.payments.dto.PaymentResponse;
import com.applicacorp.payments.entity.Payment;
import com.applicacorp.payments.entity.PaymentStatus;
import com.applicacorp.payments.integration.core.CorePaymentSender;
import com.applicacorp.payments.mapper.PaymentXmlMapper;
import com.applicacorp.payments.repository.PaymentRepository;
import org.springframework.stereotype.Service;

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
}
