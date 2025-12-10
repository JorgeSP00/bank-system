package com.bank.transactionservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.transactionservice.dto.message.TransactionRequestedMessage;
import com.bank.transactionservice.dto.request.TransactionRequestDTO;
import com.bank.transactionservice.dto.response.TransactionResponseDTO;
import com.bank.transactionservice.event.producer.TransactionCompletedRequestedEvent;
import com.bank.transactionservice.mapper.TransactionMapper;
import com.bank.transactionservice.model.account.Account;
import com.bank.transactionservice.model.account.AccountStatus;
import com.bank.transactionservice.model.transaction.Transaction;
import com.bank.transactionservice.model.transaction.TransactionStatus;
import com.bank.transactionservice.repository.TransactionRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.bank.transactionservice.exception.InvalidTransactionData;
import com.bank.transactionservice.exception.TransactionNotFound;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final OutboxEventService outboxEventService;
    private final TransactionMapper transactionMapper;

    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(transactionMapper::fromEntityToResponse)
                .collect(Collectors.toList());
    }

    public TransactionResponseDTO getTransactionById(UUID id) {
        Transaction t = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFound("Transaction with ID " + id + " not found"));
        return transactionMapper.fromEntityToResponse(t);
    }

    @Transactional
    public TransactionResponseDTO createTransaction(TransactionRequestDTO transactionRequestDTO) {
        Transaction t = validateTransaction(transactionRequestDTO);
        TransactionRequestedMessage message = mapTransactionToMessage(t);
        saveTransactionRequestedToOutbox(t.getId(), message);
        return transactionMapper.fromEntityToResponse(t);
    }

    private Transaction validateTransaction(TransactionRequestDTO transactionRequestDTO) {
        Account fromAccount = accountService.getByAccountNumber(transactionRequestDTO.getFromAccountNumber());
        Account toAccount = accountService.getByAccountNumber(transactionRequestDTO.getToAccountNumber());
        if (!fromAccount.getStatus().equals(AccountStatus.ACTIVE) || !toAccount.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new InvalidTransactionData("One or both accounts are not ACTIVE");
        }
        Transaction t = transactionMapper.fromRequestToEntity(transactionRequestDTO);
        t.setId(UUID.randomUUID());
        t.setFromAccount(fromAccount);
        t.setToAccount(toAccount);
        t.setFromAccountVersionId(fromAccount.getVersionId());
        t.setToAccountVersionId(toAccount.getVersionId());
        t.setStatus(TransactionStatus.PENDING);
        t.setObservations("Started Transaction");
        Transaction saved = transactionRepository.save(t);
        return saved;
    }

    private TransactionRequestedMessage mapTransactionToMessage(Transaction transaction) {
        return new TransactionRequestedMessage(
            transaction.getId(),
            transaction.getFromAccount().getId(),
            transaction.getFromAccountVersionId(),
            transaction.getToAccount().getId(),
            transaction.getToAccountVersionId(),
            transaction.getAmount()
        );
    }

    private void saveTransactionRequestedToOutbox(UUID transactionId, TransactionRequestedMessage message) {
        outboxEventService.saveOutboxEvent(
            "Transaction",
            transactionId,
            "TransactionRequestedMessage",
            "transaction.requested",
            message
        );
    }

    public void updateTransaction(TransactionCompletedRequestedEvent transactionCompleted) {
        Transaction transaction = transactionRepository.findById(transactionCompleted.transactionId())
                .orElseThrow(() -> new TransactionNotFound("Transaction with ID " + transactionCompleted.transactionId() + " not found"));
        transaction.setStatus(transactionCompleted.transactionStatus());
        transaction.setObservations(transactionCompleted.observations());
        transactionRepository.save(transaction);
    }
}