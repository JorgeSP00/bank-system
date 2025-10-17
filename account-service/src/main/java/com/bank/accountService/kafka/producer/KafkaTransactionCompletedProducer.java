package com.bank.accountservice.kafka.producer;


import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.bank.accountservice.event.producer.TransactionCompletedRequestedEvent;

@Service
@RequiredArgsConstructor
public class KafkaTransactionCompletedProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendTransactionConfirmedRequested(TransactionCompletedRequestedEvent event) {
        kafkaTemplate.send("transaction.completed", event);
        System.out.println("✅ [Producer] Published TransactionCompletedRequestedEvent: " + event);
    }
}
