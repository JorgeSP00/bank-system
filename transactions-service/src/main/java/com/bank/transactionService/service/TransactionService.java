package com.bank.transactionservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.bank.transactionservice.dto.request.TransactionRequestDTO;
import com.bank.transactionservice.dto.response.TransactionResponseDTO;
import com.bank.transactionservice.event.TransactionCompletedRequestedEvent;
import com.bank.transactionservice.event.TransactionRequestedEvent;
import com.bank.transactionservice.kafka.producer.KafkaTransactionProducer;
import com.bank.transactionservice.mapper.TransactionMapper;
import com.bank.transactionservice.model.account.Account;
import com.bank.transactionservice.model.account.AccountStatus;
import com.bank.transactionservice.model.transaction.Transaction;
import com.bank.transactionservice.model.transaction.TransactionStatus;
import com.bank.transactionservice.repository.TransactionRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final KafkaTransactionProducer kafkaTransactionProducer;
    private final TransactionMapper transactionMapper;

    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(transactionMapper::fromEntityToResponse)
                .collect(Collectors.toList());
    }

    public TransactionResponseDTO getTransactionById(UUID id) {
        Transaction t = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return transactionMapper.fromEntityToResponse(t);
    }
    public TransactionResponseDTO createTransaction(TransactionRequestDTO transactionRequestDTO) {
        Transaction t = validateTransaction(transactionRequestDTO);
        sendTransactionRequested(transactionMapper.fromTransactionToMessage(t));
        return transactionMapper.fromEntityToResponse(t);
    }

    private Transaction validateTransaction(TransactionRequestDTO transactionRequestDTO) {
        Account fromAccount = accountService.getByAccountNumber(transactionRequestDTO.getFromAccountNumber());
        Account toAccount = accountService.getByAccountNumber(transactionRequestDTO.getToAccountNumber());
        if (!fromAccount.getStatus().equals(AccountStatus.ACTIVE) || !toAccount.getStatus().equals(AccountStatus.ACTIVE)) {
            //error
            throw new RuntimeException("Accounts not available");
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

    private void sendTransactionRequested(TransactionRequestedEvent transactionRequestedEvent) {
        kafkaTransactionProducer.sendTransactionRequested(transactionRequestedEvent);
    }

    public void updateTransaction(TransactionCompletedRequestedEvent transactionCompleted) {
        Transaction transaction = transactionRepository.findById(transactionCompleted.transactionId())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        transaction.setStatus(transactionCompleted.transactionStatus());
        transaction.setObservations(transactionCompleted.observations());
        transactionRepository.save(transaction);
    }
}