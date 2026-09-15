package com.applicacorp.payments.mapper;

import com.applicacorp.payments.entity.Payment;
import com.applicacorp.payments.integration.core.CorePaymentXml;
import org.springframework.stereotype.Component;
import tools.jackson.dataformat.xml.XmlMapper;

import java.time.format.DateTimeFormatter;

@Component
public class PaymentXmlMapper {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private final XmlMapper xmlMapper;

    public PaymentXmlMapper() {
        this.xmlMapper = XmlMapper.builder().build();
    }

    public String toXml(Payment payment) {

        CorePaymentXml corePayment = new CorePaymentXml(
                payment.getId(),
                payment.getCustomerId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getTimestamp().format(TIMESTAMP_FORMATTER),
                payment.getStatus().name()
        );

        return xmlMapper.writeValueAsString(corePayment);
    }
}