package com.bank.transactionservice.kafka.consumer;

import com.bank.transactionservice.event.consumer.AccountProcessedEvent;
import com.bank.transactionservice.service.AccountService;

import lombok.RequiredArgsConstructor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaAccountUpdatedConsumer {
    
    private final AccountService accountService;

    @KafkaListener(
        topics = "account.updated", 
        groupId = "transaction-service-group",
        containerFactory = "accountProcessedEventKafkaListenerContainerFactory"
    )
    public void consumeAccountUpdated(AccountProcessedEvent event) {
        System.out.println("🔄 [Consumer] Received AccountUpdatedEvent: " + event);
        accountService.updateAccountFromConsumer(event);
        System.out.println("🔄 [Consumer] Account updated successfully");
    }
}
