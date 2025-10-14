package com.bank.transactionservice.dto.request;

import java.math.BigDecimal;

import com.bank.transactionservice.model.transaction.TransactionType;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDTO {
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
}