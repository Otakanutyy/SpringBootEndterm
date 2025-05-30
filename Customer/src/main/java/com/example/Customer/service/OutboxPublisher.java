package com.example.Customer.service;

import com.example.Customer.model.OutboxEvent;
import com.example.Customer.model.PaymentResultConverter;
import com.example.Customer.model.replies.CustomerPaymentResult;
import com.example.Customer.repository.OutboxRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class OutboxPublisher {
    private static final Logger log = LogManager.getLogger(OutboxPublisher.class);

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, CustomerPaymentResult> paymentKafka;
    private final PaymentResultConverter converter;

    @Value("${app.kafka.payment-result-topic}")
    private String paymentResultTopic;

    public OutboxPublisher(
            OutboxRepository outboxRepository,
            KafkaTemplate<String, CustomerPaymentResult> paymentKafka,
            PaymentResultConverter converter
    ) {
        this.outboxRepository = outboxRepository;
        this.paymentKafka     = paymentKafka;
        this.converter        = converter;
    }

    @Scheduled(fixedDelay = 1_000)
    public void publishOutboxEvents() {
        List<OutboxEvent> events = outboxRepository.findAllBySentFalse();

        for (OutboxEvent ev : events) {
            CustomerPaymentResult result = converter.convert(ev.getType(), ev.getPayload());
            CompletableFuture<SendResult<String, CustomerPaymentResult>> future = paymentKafka.send(paymentResultTopic, result);
            future.thenAccept(r -> {
                log.info("Message was sent: {}", result);
                ev.setSent(true);
                outboxRepository.save(ev);
            }).exceptionally(ex -> {
                log.info("Message failed to send: {}, Error: ", result, ex);
                return null;
            });
        }

    }
}
