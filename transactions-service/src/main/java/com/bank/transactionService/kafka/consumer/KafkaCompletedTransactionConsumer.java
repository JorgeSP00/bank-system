package com.bank.transactionservice.kafka.consumer;

import com.bank.transactionservice.event.producer.TransactionCompletedRequestedEvent;
import com.bank.transactionservice.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaCompletedTransactionConsumer {
    
    private final TransactionService transactionService;

    @KafkaListener(
        topics = "transaction.completed", 
        groupId = "transaction-service-group",
        containerFactory = "transactionCompletedEventKafkaListenerContainerFactory"
    )
    public void consume(TransactionCompletedRequestedEvent event) throws JsonProcessingException {
        // ObjectMapper mapper = new ObjectMapper();
        
        // // Mapear desde JSON string a Map para controlar conversiones de tipos
        // var eventMap = mapper.readValue(message, java.util.Map.class);
        
        // // Convertir manualmente los tipos
        // UUID transactionId = UUID.fromString((String) eventMap.get("transactionId"));
        // TransactionStatus transactionStatus = TransactionStatus.valueOf((String) eventMap.get("transactionStatus"));
        // String observations = (String) eventMap.get("observations");
        
        // TransactionCompletedRequestedEvent event = new TransactionCompletedRequestedEvent(transactionId, transactionStatus, observations);
        // System.out.println("📩 [Consumer] Received TransactionCompletedRequestedEvent: " + event);

        System.out.println("-----------------------------------------------");

        transactionService.updateTransaction(event);
    }
}
