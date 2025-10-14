package com.bank.transactionservice.mapper;


import org.springframework.stereotype.Component;

import com.bank.transactionservice.dto.request.TransactionRequestDTO;
import com.bank.transactionservice.dto.response.TransactionResponseDTO;
import com.bank.transactionservice.event.TransactionRequestedEvent;
import com.bank.transactionservice.model.transaction.Transaction;

@Component
public class TransactionMapper {

    public Transaction fromRequestToEntity(TransactionRequestDTO dto) {
        return Transaction.builder()
                .amount(dto.getAmount())
                .description(dto.getDescription())
                .type(dto.getType())
                .build();
    }

    public TransactionResponseDTO fromEntityToResponse(Transaction t) {
        return TransactionResponseDTO.builder()
                .transactionId(t.getId())
                .fromAccountNumber(t.getFromAccount().getAccountNumber())
                .toAccountNumber(t.getToAccount().getAccountNumber())
                .amount(t.getAmount())
                .type(t.getType())
                .status(t.getStatus())
                .description(t.getDescription())
                .createdAt(t.getCreatedAt())
                .build();
    }

    public TransactionRequestedEvent fromTransactionToMessage(Transaction transaction) {
        return new TransactionRequestedEvent(transaction.getId(), transaction.getFromAccount().getId(), transaction.getFromAccount().getVersionId(), 
            transaction.getToAccount().getId(), transaction.getToAccount().getVersionId(), transaction.getAmount());
    }
}

