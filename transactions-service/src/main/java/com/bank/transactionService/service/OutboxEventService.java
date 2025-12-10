package com.bank.transactionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.bank.transactionservice.model.outbox.OutboxEvent;
import com.bank.transactionservice.repository.OutboxEventRepository;
import com.bank.transactionservice.exception.EventSerializationException;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public void saveOutboxEvent(String aggregateType, UUID aggregateId, String eventType, String topic, Object payload) {
        try {
            // Serializar el payload a JSON string
            String payloadJson = objectMapper.writeValueAsString(payload);

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .type(eventType)
                    .topic(topic)
                    .payload(payloadJson)
                    .build();

            outboxEventRepository.save(outboxEvent);
            
            log.info("📌 [OutboxEventService] Saved outbox event - Type: {}, Topic: {}, AggregateId: {}", 
                eventType, topic, aggregateId);
        } catch (Exception e) {
            log.error("❌ [OutboxEventService] Failed to save outbox event", e);
            throw new EventSerializationException("Failed to save outbox event", e);
        }
    }
}
