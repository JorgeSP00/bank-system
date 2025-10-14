package com.bank.accountService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.accountService.model.account.Account;
import com.bank.accountService.model.account.AccountDTO;
import com.bank.accountService.model.account.AccountRequestedEvent;
import com.bank.accountService.service.AccountService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/bank_system/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public List<AccountDTO> getAll() {
        List<AccountDTO> allAccounts = accountService.findAllAccounts()
            .stream()
            .map(t -> accountService.accountToDto(t))
            .collect(Collectors.toList());
        return allAccounts;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.accountToDto(accountService.getAccountById(id)));
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO dto) {
        Account account = accountService.createOrUpdateAccount(dto);
        accountService.sendAccountRequested(
            new AccountRequestedEvent(account.getId(), account.getAccountNumber(), account.getStatus(), account.getVersionId())
        );
        return ResponseEntity.ok(accountService.accountToDto(account));
    }

    @PutMapping
    public ResponseEntity<AccountDTO> updateAccount(@RequestBody AccountDTO dto) {
        return ResponseEntity.ok(accountService.accountToDto(accountService.createOrUpdateAccount(dto)));
    }
}