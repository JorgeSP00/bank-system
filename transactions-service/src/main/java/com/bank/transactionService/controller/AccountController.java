package com.bank.transactionService.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bank.transactionService.model.account.Account;
import com.bank.transactionService.model.account.AccountDTO;
import com.bank.transactionService.service.AccountService;
import java.util.List;

@RestController
@RequestMapping("/bank_system/transactionservice/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/account")
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/account/{id}")
    public ResponseEntity<AccountDTO> getAccountByAccountNumer(@PathVariable String id) {
        Account account = accountService.getByAccountNumber(id);
        return ResponseEntity.ok(accountService.accountToDto(account));
    }

    @PostMapping("/account")
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO dto) {
        return ResponseEntity.ok(accountService.saveOrUpdateDTO(dto));
    }

    @PutMapping("/account")
    public ResponseEntity<AccountDTO> updateAccount(@RequestBody AccountDTO dto) {
        return ResponseEntity.ok(accountService.saveOrUpdateDTO(dto));
    }
}