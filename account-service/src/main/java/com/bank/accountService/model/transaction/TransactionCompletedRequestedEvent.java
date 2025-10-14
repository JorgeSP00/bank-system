package com.bank.accountService.model.transaction;

import java.util.UUID;

public record TransactionCompletedRequestedEvent(
    UUID transactionId,
    TransactionStatus transactionStatus,
    String observations
) {}
