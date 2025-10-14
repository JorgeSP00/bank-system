package com.bank.accountService.model.transaction;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionProcessedEvent(
    UUID transactionId,
    UUID fromAccountId,
    UUID fromAccountVersionId,
    UUID toAccountId,
    UUID toAccountVersionId,
    BigDecimal amount
) {}
