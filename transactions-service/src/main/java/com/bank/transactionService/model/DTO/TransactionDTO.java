package com.bank.transactionService.model.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private String type;
    private String description;
    private LocalDateTime createdAt;
}