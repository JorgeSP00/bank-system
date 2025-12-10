package com.bank.accountservice.model.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String aggregateType; // ej: "Account"

    @Column(nullable = false)
    private UUID aggregateId; // id de la entidad principal

    @Column(nullable = false)
    private String type; // ej: "AccountRequestedEvent"

    @Column
    private String topic; // ej: "account.requested" (opcional, si está vacío se usará `type`)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload; // JSON serializado del evento

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OutboxStatus status; // 
    

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Instant sentAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        status = OutboxStatus.PENDING;
    }

}
