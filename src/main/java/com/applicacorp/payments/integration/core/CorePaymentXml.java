package com.applicacorp.payments.integration.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@JacksonXmlRootElement(localName = "payment")
public class CorePaymentXml {

    @JacksonXmlProperty(localName = "id")
    private final String id;

    @JacksonXmlProperty(localName = "customerId")
    private final String customerId;

    @JacksonXmlProperty(localName = "amount")
    private final BigDecimal amount;

    @JacksonXmlProperty(localName = "currency")
    private final String currency;

    @JacksonXmlProperty(localName = "timestamp")
    private final String timestamp;

    @JacksonXmlProperty(localName = "status")
    private final String status;
}
