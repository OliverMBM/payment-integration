package com.applicacorp.payments.mapper;

import com.applicacorp.payments.entity.Payment;
import com.applicacorp.payments.entity.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentXmlMapperTest {

    private final PaymentXmlMapper mapper = new PaymentXmlMapper();

    @Test
    void shouldTransformPaymentToXml() {

        Payment payment = new Payment(
                "PAY-001",
                "CUS-100",
                new BigDecimal("150.75"),
                "USD",
                OffsetDateTime.parse("2026-09-14T20:30:00-06:00"),
                PaymentStatus.PROCESSED
        );

        String xml = mapper.toXml(payment);
        System.out.println(xml);

        assertThat(xml)
                .contains("<payment>")
                .contains("<id>PAY-001</id>")
                .contains("<customerId>CUS-100</customerId>")
                .contains("<amount>150.75</amount>")
                .contains("<currency>USD</currency>")
                .contains("<timestamp>2026-09-14T20:30:00-06:00</timestamp>")
                .contains("<status>PROCESSED</status>")
                .contains("</payment>");
    }
}