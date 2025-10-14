package com.bank.transactionservice.kafka.consumer;

import com.bank.transactionservice.event.TransactionCompletedRequestedEvent;
import com.bank.transactionservice.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaCompletedAccountConsumer {
    
    private final TransactionService transactionService;

    @KafkaListener(topics = "transaction.completed", groupId = "transaction-service-group")
    public void consume(String message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        TransactionCompletedRequestedEvent event = mapper.readValue(message, TransactionCompletedRequestedEvent.class);
        System.out.println("📩 [Consumer] Received AccountProcessedEvent: " + event);

        System.out.println("-----------------------------------------------");

        transactionService.updateTransaction(event);
    }
}
