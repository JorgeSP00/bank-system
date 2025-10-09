package com.bank.transactionService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.bank.transactionService.model.Account;
import com.bank.transactionService.model.Transaction;
import com.bank.transactionService.model.TransactionType;
import com.bank.transactionService.model.DTO.TransactionDTO;
import com.bank.transactionService.repository.AccountRepository;
import com.bank.transactionService.repository.TransactionRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(this::transactionToDto)
                .collect(Collectors.toList());
    }

    public TransactionDTO getTransactionById(UUID id) {
        return transactionRepository.findById(id)
                .map(this::transactionToDto)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public TransactionDTO createTransaction(TransactionDTO dto) {
        Transaction t = new Transaction();
        Account fromAccount = accountRepository.findByAccountNumber(dto.getFromAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta origen no encontrada"));

        Account toAccount = accountRepository.findByAccountNumber(dto.getToAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta destino no encontrada"));
        t.setFromAccountId(fromAccount.getId());
        t.setToAccountId(toAccount.getId());
        t.setFromAccountVersionId(fromAccount.getVersionId());
        t.setToAccountVersionId(toAccount.getVersionId());
        t.setAmount(dto.getAmount());
        t.setType(TransactionType.valueOf(dto.getType().toUpperCase()));
        t.setDescription(dto.getDescription());
        Transaction saved = transactionRepository.save(t);
        return transactionToDto(saved);
    }

    // 🔁 Métodos de conversión
    private TransactionDTO transactionToDto(Transaction t) {
        String fromAccountNumber = accountRepository.findById(t.getFromAccountId())
                .map(Account::getAccountNumber)
                .orElse("UNKNOWN");

        String toAccountNumber = accountRepository.findById(t.getToAccountId())
                .map(Account::getAccountNumber)
                .orElse("UNKNOWN");

        return TransactionDTO.builder()
                .fromAccountNumber(fromAccountNumber)
                .toAccountNumber(toAccountNumber)
                .amount(t.getAmount())
                .type(t.getType().toString())
                .description(t.getDescription())
                .createdAt(t.getCreatedAt())
                .build();
    }
}