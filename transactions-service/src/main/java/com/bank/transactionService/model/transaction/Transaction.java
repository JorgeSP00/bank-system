package com.bank.transactionService.model.transaction;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

import com.bank.transactionService.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
@Entity
@Table(name = "transaction")
@SuperBuilder
@NoArgsConstructor
public class Transaction extends BaseEntity {

    @Column(nullable = false)
    private UUID fromAccountId;

    @Column(nullable = false)
    private UUID toAccountId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private UUID fromAccountVersionId;

    @Column(nullable = false)
    private UUID toAccountVersionId;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false)
    private String observations;
}