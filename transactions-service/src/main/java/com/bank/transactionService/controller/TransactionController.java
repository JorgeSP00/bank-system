package com.bank.transactionService.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bank.transactionService.model.account.Account;
import com.bank.transactionService.model.account.AccountStatus;
import com.bank.transactionService.model.transaction.Transaction;
import com.bank.transactionService.model.transaction.TransactionDTO;
import com.bank.transactionService.model.transaction.TransactionRequestedEvent;
import com.bank.transactionService.service.AccountService;
import com.bank.transactionService.service.TransactionService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bank_system/transactionserive/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<TransactionDTO> allTransactions= transactionService.getAllTransactions()
            .stream()
            .map(t -> transactionService.transactionToDto(t))
            .collect(Collectors.toList());
        return ResponseEntity.ok(allTransactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.transactionToDto(transactionService.getTransactionById(id)));
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDTO) throws Exception {
        Account fromAccount = accountService.getByAccountNumber(transactionDTO.getFromAccountNumber());
        Account toAccount = accountService.getByAccountNumber(transactionDTO.getToAccountNumber());
        if(!fromAccount.getStatus().equals(AccountStatus.ACTIVE) || !fromAccount.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new Exception("Accounts not valid");
        }
        Transaction transaction = transactionService.createTransaction(transactionDTO);
        transactionService.sendTransactionRequested(
            new TransactionRequestedEvent(transaction.getId(), fromAccount.getId(), fromAccount.getVersionId(), toAccount.getId(), toAccount.getVersionId(), transaction.getAmount())
        );
        return ResponseEntity.ok(transactionService.transactionToDto(transaction));
    }
}