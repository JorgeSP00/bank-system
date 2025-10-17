package com.bank.accountservice.kafka.consumer;

import com.bank.accountservice.event.consumer.TransactionProcessedEvent;
import com.bank.accountservice.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaTransactionConsumer {
    
    private final TransactionService transactionService;

    @KafkaListener(topics = "transaction.requested", groupId = "account-service-group")
    public void consume(String message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        TransactionProcessedEvent event = mapper.readValue(message, TransactionProcessedEvent.class);
        transactionService.doTransaction(event);
        System.out.println("📩 [Consumer] Received: " + event);
    }
}
