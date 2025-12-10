package com.bank.transactionservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.bank.transactionservice.event.consumer.AccountProcessedEvent;
import com.bank.transactionservice.event.producer.TransactionCompletedRequestedEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    /**
     * Consumer factory para AccountProcessedEvent
     * Deserializa records de los topics "account.created" y "account.updated"
     */
    @Bean
    public ConsumerFactory<String, AccountProcessedEvent> accountProcessedEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, AccountProcessedEvent.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.bank.transactionservice.event.consumer");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AccountProcessedEvent> accountProcessedEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountProcessedEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());
        factory.setConsumerFactory(accountProcessedEventConsumerFactory());
        return factory;
    }

    /**
     * Consumer factory para TransactionCompletedRequestedEvent
     * Deserializa records del topic "transaction.completed"
     */
    @Bean
    public ConsumerFactory<String, TransactionCompletedRequestedEvent> transactionCompletedEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, TransactionCompletedRequestedEvent.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.bank.transactionservice.event.producer");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionCompletedRequestedEvent> transactionCompletedEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransactionCompletedRequestedEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());
        factory.setConsumerFactory(transactionCompletedEventConsumerFactory());
        return factory;
    }
}
