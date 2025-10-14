package com.bank.accountService.service;


import com.bank.accountService.model.transaction.TransactionCompletedRequestedEvent;

import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaTransactionCompletedProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendTransactionConfirmedRequested(TransactionCompletedRequestedEvent event) {
        kafkaTemplate.send("transaction.completed", event);
        System.out.println("✅ [Producer] Published TransactionCompletedRequestedEvent: " + event);
    }
}
