package com.applicacorp.payments.integration.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class CorePaymentSender {

    private final Path outboxDirectory;

    public CorePaymentSender(@Value("${app.core.outbox-dir:outbox}") String outboxDirectory) {

        this.outboxDirectory = Path.of(outboxDirectory);
    }

    public void send(String paymentId, String xml) {
        try {
            Files.createDirectories(outboxDirectory);

            Path outputFile = outboxDirectory.resolve(
                    "payment-" + paymentId + ".xml"
            );

            Files.writeString(
                    outputFile,
                    xml,
                    StandardCharsets.UTF_8
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not send payment to core system",
                    exception
            );
        }
    }
}
