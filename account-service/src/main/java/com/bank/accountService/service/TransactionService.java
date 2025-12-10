package com.bank.accountservice.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.accountservice.event.consumer.TransactionProcessedEvent;
import com.bank.accountservice.exception.EventSerializationException;
import com.bank.accountservice.model.account.Account;
import com.bank.accountservice.model.account.AccountStatus;
import com.bank.accountservice.model.transaction.TransactionStatus;
import com.bank.accountservice.model.outbox.OutboxEvent;
import com.bank.accountservice.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final AccountService accountService;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void doTransaction(TransactionProcessedEvent transactionProcessedEvent) {
        TransactionStatus transactionState;
        Account fromAccount = accountService.getAccountById(transactionProcessedEvent.fromAccountId());
        Account toAccount = accountService.getAccountById(transactionProcessedEvent.toAccountId());
        
        if (checkAccounts(transactionProcessedEvent, fromAccount, toAccount) && updateAccounts(transactionProcessedEvent, fromAccount, toAccount)) {
            transactionState = TransactionStatus.CORRECT;
        } else {
            transactionState = TransactionStatus.INCORRECT;
        }
        completeTransaction(transactionProcessedEvent, transactionState);
    }

    private boolean checkAccounts(TransactionProcessedEvent transactionProcessedEvent, Account fromAccount, Account toAccount) {
        if (fromAccount.getVersionId().equals(transactionProcessedEvent.fromAccountVersionId()) && fromAccount.getStatus().equals(AccountStatus.ACTIVE) && fromAccount.getBalance().compareTo(transactionProcessedEvent.amount()) != -1 &&
            toAccount.getVersionId().equals(transactionProcessedEvent.toAccountVersionId()) && toAccount.getStatus().equals(AccountStatus.ACTIVE)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean updateAccounts (TransactionProcessedEvent transactionProcessedEvent, Account fromAccount, Account toAccount) {
        fromAccount.setBalance(fromAccount.getBalance().subtract(transactionProcessedEvent.amount()));
        toAccount.setBalance(toAccount.getBalance().add(transactionProcessedEvent.amount()));
        accountService.updateAccount(fromAccount);
        accountService.updateAccount(toAccount);
        return true;
    }

    private void completeTransaction(TransactionProcessedEvent transactionProcessedEvent, TransactionStatus transactionState) {
        OutboxEvent outboxEvent;
        try {
            Map<String, Object> payload = Map.of(
                "transactionId", transactionProcessedEvent.transactionId().toString(),
                "transactionStatus", transactionState.name(),
                "observations", "null"
            );
            String payloadJson = objectMapper.writeValueAsString(payload);
            outboxEvent = OutboxEvent.builder()
                .aggregateType("Transaction")
                .aggregateId(transactionProcessedEvent.transactionId())
                .type("TransactionCompletedRequestedEvent")
                .topic("transaction.completed")
                .payload(payloadJson)
                .build();
        } catch (JsonProcessingException e) {
            throw new EventSerializationException("Failed to serialize TransactionCompletedRequestedEvent", e);
        }
        outboxEventRepository.save(outboxEvent);
    }
}
