package com.bank.transactionservice.kafka.consumer;

import com.bank.transactionservice.event.consumer.AccountProcessedEvent;
import com.bank.transactionservice.service.AccountService;

import lombok.RequiredArgsConstructor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaAccountCreatedConsumer {
    
    private final AccountService accountService;

    @KafkaListener(
        topics = "account.created", 
        groupId = "transaction-service-group",
        containerFactory = "accountProcessedEventKafkaListenerContainerFactory"
    )
    public void consumeAccountCreated(AccountProcessedEvent event) {
        System.out.println("✅ [Consumer] Received AccountCreatedEvent: " + event);
        accountService.createAccountFromConsumer(event);
        System.out.println("✅ [Consumer] Account created successfully");
    }
}
