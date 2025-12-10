package com.bank.transactionservice.kafka.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bank.transactionservice.model.outbox.OutboxEvent;
import com.bank.transactionservice.model.outbox.OutboxEventStatus;
import com.bank.transactionservice.repository.OutboxEventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatus(OutboxEventStatus.PENDING);

        for (OutboxEvent event : pendingEvents) {
            try {
                // Enviar evento a Kafka como string (payload ya está serializado)
                kafkaTemplate.send(event.getTopic(), event.getPayload());
                
                // Marcar como enviado
                event.setStatus(OutboxEventStatus.SENT);
                event.setSentAt(LocalDateTime.now());
                outboxEventRepository.save(event);
                
                log.info("✅ [OutboxPublisher] Published event - Topic: {}, EventType: {}, AggregateId: {}", 
                    event.getTopic(), event.getType(), event.getAggregateId());
            } catch (Exception e) {
                log.error("❌ [OutboxPublisher] Failed to publish event - Topic: {}, AggregateId: {}", 
                    event.getTopic(), event.getAggregateId(), e);
                
                // Marcar como fallido
                event.setStatus(OutboxEventStatus.FAILED);
                outboxEventRepository.save(event);
            }
        }
    }
}
