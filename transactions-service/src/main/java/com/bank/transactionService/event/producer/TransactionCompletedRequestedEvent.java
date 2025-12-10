package com.bank.transactionservice.event.producer;

import java.util.UUID;

import com.bank.transactionservice.model.transaction.TransactionStatus;

public record TransactionCompletedRequestedEvent(
    UUID transactionId,
    TransactionStatus transactionStatus,
    String observations
) {}
