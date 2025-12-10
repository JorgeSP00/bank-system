package com.bank.accountservice.kafka.publisher;

import java.time.Instant;
import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bank.accountservice.model.outbox.OutboxEvent;
import com.bank.accountservice.model.outbox.OutboxStatus;
import com.bank.accountservice.repository.OutboxEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000) // cada 5 segundos
    @Transactional
    public void publishPendingEvents() {
        // fallback: read all and filter by status to avoid relying on custom repository method at compile time
        List<OutboxEvent> events = outboxEventRepository.findAll();

        for (OutboxEvent event : events) {
            if (event.getStatus() != OutboxStatus.PENDING) continue;
            try {
                String topic = event.getTopic() != null && !event.getTopic().isBlank() ? event.getTopic() : event.getType();
                kafkaTemplate.send(topic, event.getPayload()).get(); // bloquea hasta confirmar
                event.setStatus(OutboxStatus.SENT);
                event.setSentAt(Instant.now());
                outboxEventRepository.save(event);
            } catch (Exception e) {
                event.setStatus(OutboxStatus.FAILED);
                outboxEventRepository.save(event);
            }
        }
    }
}
