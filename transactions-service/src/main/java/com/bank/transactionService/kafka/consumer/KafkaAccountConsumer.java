package com.bank.transactionservice.kafka.consumer;

import com.bank.transactionservice.event.AccountProcessedEvent;
import com.bank.transactionservice.service.AccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaAccountConsumer {
    
    private final AccountService accountService;

    @KafkaListener(topics = "account.requested", groupId = "transaction-service-group")
    public void consume(String message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        AccountProcessedEvent event = mapper.readValue(message, AccountProcessedEvent.class);
        System.out.println("📩 [Consumer] Received AccountProcessedEvent: " + event);

        System.out.println("-----------------------------------------------");

        accountService.saveOrUpdateFromConsumer(event);
    }
}
