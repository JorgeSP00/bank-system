package com.bank.accountservice.event;

import java.util.UUID;

import com.bank.accountservice.model.transaction.TransactionStatus;

public record TransactionCompletedRequestedEvent(
    UUID transactionId,
    TransactionStatus transactionStatus,
    String observations
) {}
