package com.bank.accountservice.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.accountservice.dto.request.AccountRequestDTO;
import com.bank.accountservice.dto.response.AccountResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bank.accountservice.exception.AccountAlreadyExists;
import com.bank.accountservice.exception.AccountNotFound;
import com.bank.accountservice.exception.EventSerializationException;
import com.bank.accountservice.mapper.AccountMapper;
import com.bank.accountservice.model.account.Account;
import com.bank.accountservice.model.outbox.OutboxEvent;
import com.bank.accountservice.repository.AccountRepository;
import com.bank.accountservice.repository.OutboxEventRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    

    private final AccountRepository accountRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final EntityManager entityManager;

    private final AccountMapper accountMapper;
    private final ObjectMapper objectMapper;

    public List<AccountResponseDTO> findAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(accountMapper::fromEntityToResponse)
                .collect(Collectors.toList());
    }

    public AccountResponseDTO getAccountResponseDTOById(UUID id) {
        Account a = getAccountById(id);
        return accountMapper.fromEntityToResponse(a);
    }

    public Account getAccountById(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFound("Account with ID " + id + " not found"));
    }

    @Transactional
    public AccountResponseDTO updateAccountResponseDTO(UUID id, AccountRequestDTO accountRequestDTO) {
        Optional<Account> existing = accountRepository.findById(id);
        Account account;
        if(existing.isPresent()) {
            account = existing.get();
            //Revisar que actualice la versión.
            account.setAccountNumber(accountRequestDTO.getAccountNumber());
            account.setOwnerName(accountRequestDTO.getOwnerName());
            account.setStatus(accountRequestDTO.getStatus());
            account.setBalance(accountRequestDTO.getBalance());
            Account saved = updateAccount(account);
            saveAccountUpdatedEvent(saved);
            return accountMapper.fromEntityToResponse(saved);
        } else {
            throw new AccountNotFound("Account with ID " + id + " not found");
        }
    }

    @Transactional
    public Account updateAccount(Account account) {
        Account saved = accountRepository.save(account);
        accountRepository.flush();  // Fuerza la escritura a BD
        entityManager.refresh(saved);  // Recarga desde BD los valores reales (triggers, defaults, etc.)
        return saved;
    }

    @Transactional
    public AccountResponseDTO createAccountResponseDTO(AccountRequestDTO accountRequestDTO) {
        Optional<Account> existing = accountRepository.findByAccountNumber(accountRequestDTO.getAccountNumber());
        Account account;
        if (existing.isPresent()) {
            throw new AccountAlreadyExists("Account with account number " + accountRequestDTO.getAccountNumber() + "already exists");
        }
        else {
            account = Account.builder()
                .accountNumber(accountRequestDTO.getAccountNumber())
                .ownerName(accountRequestDTO.getOwnerName())
                .balance(accountRequestDTO.getBalance())
                .status(accountRequestDTO.getStatus())
                .build();
            Account saved = accountRepository.save(account);
            saveAccountCreatedEvent(saved);
            return accountMapper.fromEntityToResponse(saved);
        }
    }


    private void saveAccountCreatedEvent(Account account) {
        OutboxEvent outboxEvent;
        try {
            outboxEvent = OutboxEvent.builder()
                .aggregateType("Account")
                .aggregateId(account.getId())
                .type("AccountCreatedEvent")
                .topic("account.created")
                .payload(objectMapper.writeValueAsString(
                    accountMapper.fromEntityToMessage(account))
                )
                .build();
        } catch (JsonProcessingException e) {
            throw new EventSerializationException("Failed to serialize AccountCreatedEvent", e);
        }
        outboxEventRepository.save(outboxEvent);
    }

    private void saveAccountUpdatedEvent(Account account) {
        OutboxEvent outboxEvent;
        try {
            outboxEvent = OutboxEvent.builder()
                .aggregateType("Account")
                .aggregateId(account.getId())
                .type("AccountUpdatedEvent")
                .topic("account.updated")
                .payload(objectMapper.writeValueAsString(
                    accountMapper.fromEntityToMessage(account))
                )
                .build();
        } catch (JsonProcessingException e) {
            throw new EventSerializationException("Failed to serialize AccountUpdatedEvent", e);
        }
        outboxEventRepository.save(outboxEvent);
    }
}