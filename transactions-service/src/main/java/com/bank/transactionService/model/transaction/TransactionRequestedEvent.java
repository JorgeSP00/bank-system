package com.bank.transactionService.model.transaction;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionRequestedEvent(
    UUID transactionId,
    UUID fromAccountId,
    UUID fromAccountVersionId,
    UUID toAccountId,
    UUID toAccountVersionId,
    BigDecimal amount
) {}
