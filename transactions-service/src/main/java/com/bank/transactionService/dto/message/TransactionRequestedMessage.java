package com.bank.transactionservice.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestedMessage {
    private UUID transactionId;
    private UUID fromAccountId;
    private UUID fromAccountVersionId;
    private UUID toAccountId;
    private UUID toAccountVersionId;
    private BigDecimal amount;
}
