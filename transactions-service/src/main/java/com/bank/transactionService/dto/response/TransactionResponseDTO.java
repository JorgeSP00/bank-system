package com.bank.transactionservice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.bank.transactionservice.model.transaction.TransactionStatus;
import com.bank.transactionservice.model.transaction.TransactionType;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {
    private UUID transactionId;
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private TransactionStatus status;
    private LocalDateTime createdAt;
}