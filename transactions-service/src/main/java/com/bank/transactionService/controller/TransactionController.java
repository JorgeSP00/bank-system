package com.bank.transactionService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bank.transactionService.model.DTO.AccountDTO;
import com.bank.transactionService.model.DTO.TransactionDTO;
import com.bank.transactionService.service.AccountService;
import com.bank.transactionService.service.TransactionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bank_system/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    private final AccountService accountService;

    @GetMapping("/transaction")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/transaction/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @PostMapping("/transaction")
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO dto) {
        return ResponseEntity.ok(transactionService.createTransaction(dto));
    }

    @GetMapping("/account")
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/account/{id}")
    public ResponseEntity<AccountDTO> getAccountByAccountNumer(@PathVariable String id) {
        return ResponseEntity.ok(accountService.getByAccountNumber(id));
    }

    @PostMapping("/account")
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO dto) {
        return ResponseEntity.ok(accountService.saveOrUpdate(dto));
    }

    @PutMapping("/account")
    public ResponseEntity<AccountDTO> updateAccount(@RequestBody AccountDTO dto) {
        return ResponseEntity.ok(accountService.saveOrUpdate(dto));
    }
}