/**package com.bank.transactionservice.kafka.consumer;

import com.bank.transactionservice.event.consumer.AccountProcessedEvent;
import com.bank.transactionservice.service.AccountService;

import lombok.RequiredArgsConstructor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaAccountConsumer {
    
    private final AccountService accountService;

    /**
     * Este consumer se mantiene para compatibilidad con otros eventos de accounts
     * Los eventos específicos de CREATE/UPDATE son manejados por:
     * - KafkaAccountCreatedConsumer
     * - KafkaAccountUpdatedConsumer
     */
    /**
    @KafkaListener(
        topics = "account.requested", 
        groupId = "transaction-service-group",
        containerFactory = "accountProcessedEventKafkaListenerContainerFactory"
    )
    public void consume(AccountProcessedEvent event) {
        System.out.println("📩 [Consumer] Received AccountProcessedEvent (fallback): " + event);
        accountService.saveOrUpdateFromConsumer(event);
    }
}**/
