package com.bank.transactionservice.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.bank.transactionservice.event.producer.TransactionRequestedEvent;

@Service
public class KafkaTransactionProducer {

    private final KafkaTemplate<String, TransactionRequestedEvent> kafkaTemplate;

    public KafkaTransactionProducer(KafkaTemplate<String, TransactionRequestedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransactionRequested(TransactionRequestedEvent event) {
        kafkaTemplate.send("transaction.requested", event);
        System.out.println("✅ [Producer] Published TransactionRequestedEvent: " + event);
    }
}

