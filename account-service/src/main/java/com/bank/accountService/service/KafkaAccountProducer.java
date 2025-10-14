package com.bank.accountService.service;


import com.bank.accountService.model.account.AccountRequestedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

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
