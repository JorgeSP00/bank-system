package com.bank.accountService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.accountService.model.DTO.AccountDTO;
import com.bank.accountService.service.AccountService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/bank_system/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    @GetMapping
    public List<AccountDTO> getAll() {
        return service.findAllAccounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getAccountById(id));
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO dto) {
        return ResponseEntity.ok(service.createOrUpdateAccount(dto));
    }

    @PutMapping
    public ResponseEntity<AccountDTO> updateAccount(@RequestBody AccountDTO dto) {
        return ResponseEntity.ok(service.createOrUpdateAccount(dto));
    }
}