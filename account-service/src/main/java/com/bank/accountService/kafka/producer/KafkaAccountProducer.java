package com.bank.accountservice.kafka.producer;


import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.bank.accountservice.event.AccountRequestedEvent;

@Service
public class KafkaAccountProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaAccountProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAccountRequested(AccountRequestedEvent event) {
        kafkaTemplate.send("account.requested", event);
        System.out.println("✅ [Producer] Published AccountRequestedEvent: " + event);
    }
}
