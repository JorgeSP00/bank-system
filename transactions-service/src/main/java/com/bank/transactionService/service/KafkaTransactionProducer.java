package com.bank.transactionService.service;

import com.bank.transactionService.model.transaction.TransactionRequestedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

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

