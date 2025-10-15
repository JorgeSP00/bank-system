package com.bank.accountservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.accountservice.dto.request.AccountRequestDTO;
import com.bank.accountservice.dto.response.AccountResponseDTO;
import com.bank.accountservice.service.AccountService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/bank_system/accounts/")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public List<AccountResponseDTO> getAll() {
        return accountService.findAllAccounts();
    }

    @GetMapping("{id}")
    public ResponseEntity<AccountResponseDTO> getAccountById(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getAccountResponseDTOById(id));
    }

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody AccountRequestDTO dto) {
        return ResponseEntity.ok(accountService.createOrUpdateAccountResponseDTO(dto));
    }

    @PutMapping
    public ResponseEntity<AccountResponseDTO> updateAccount(@RequestBody AccountRequestDTO dto) {
        return ResponseEntity.ok(accountService.createOrUpdateAccountResponseDTO(dto));
    }
}