package com.bank.accountservice.kafka.consumer;

import com.bank.accountservice.event.consumer.TransactionProcessedEvent;
import com.bank.accountservice.service.TransactionService;

import lombok.RequiredArgsConstructor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaTransactionConsumer {
    
    private final TransactionService transactionService;

    @KafkaListener(topics = "transaction.requested", groupId = "account-service-group", 
                   containerFactory = "transactionProcessedEventKafkaListenerContainerFactory")
    public void consume(TransactionProcessedEvent event) {
        transactionService.doTransaction(event);
        System.out.println("📩 [Consumer] Received: " + event);
    }
}
