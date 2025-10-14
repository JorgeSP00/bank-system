package com.bank.transactionService.model.account;

import java.util.UUID;

public record AccountProcessedEvent(
    UUID accountId,
    String accountNumber,
    AccountStatus status,
    UUID version
) {}
