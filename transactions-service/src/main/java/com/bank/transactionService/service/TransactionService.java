package com.bank.transactionService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.bank.transactionService.model.account.Account;
import com.bank.transactionService.model.transaction.Transaction;
import com.bank.transactionService.model.transaction.TransactionCompletedRequestedEvent;
import com.bank.transactionService.model.transaction.TransactionDTO;
import com.bank.transactionService.model.transaction.TransactionRequestedEvent;
import com.bank.transactionService.model.transaction.TransactionStatus;
import com.bank.transactionService.model.transaction.TransactionType;
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
    private final KafkaTransactionProducer kafkaTransactionProducer;

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .collect(Collectors.toList());
    }

    public Transaction getTransactionById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public Transaction createTransaction(TransactionDTO dto) {
        Transaction t = new Transaction();
        Account fromAccount = accountRepository.findByAccountNumber(dto.getFromAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta origen no encontrada"));

        Account toAccount = accountRepository.findByAccountNumber(dto.getToAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta destino no encontrada"));
        t.setId(UUID.randomUUID());
        t.setFromAccountId(fromAccount.getId());
        t.setToAccountId(toAccount.getId());
        t.setFromAccountVersionId(fromAccount.getVersionId());
        t.setToAccountVersionId(toAccount.getVersionId());
        t.setAmount(dto.getAmount());
        t.setType(TransactionType.valueOf(dto.getType().toUpperCase()));
        t.setDescription(dto.getDescription());
        t.setStatus(TransactionStatus.PENDING);
        t.setObservations("Transacción empezada");
        Transaction saved = transactionRepository.save(t);
        return saved;
    }

    // 🔁 Métodos de conversión
    public TransactionDTO transactionToDto(Transaction t) {
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

    public void sendTransactionRequested(TransactionRequestedEvent transactionRequestedEvent) {
        kafkaTransactionProducer.sendTransactionRequested(transactionRequestedEvent);
    }

    public void updateTransaction(TransactionCompletedRequestedEvent transactionCompleted) {
        Transaction transaction = getTransactionById(transactionCompleted.transactionId());
        transaction.setStatus(transactionCompleted.transactionStatus());
        transaction.setObservations(transactionCompleted.observations());
        transactionRepository.save(transaction);
    }
}