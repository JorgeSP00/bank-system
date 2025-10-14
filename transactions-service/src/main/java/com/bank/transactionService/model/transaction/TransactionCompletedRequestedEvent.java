package com.bank.transactionService.model.transaction;

import java.util.UUID;

public record TransactionCompletedRequestedEvent(
    UUID transactionId,
    TransactionStatus transactionStatus,
    String observations
) {}
