package com.bank.accountService.service;

import com.bank.accountService.model.account.Account;
import com.bank.accountService.model.account.AccountDTO;
import com.bank.accountService.model.account.AccountRequestedEvent;
import com.bank.accountService.model.account.AccountStatus;
import com.bank.accountService.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final KafkaAccountProducer kafkaAccountProducer;

    private final AccountRepository accountRepository;

    public List<Account> findAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .collect(Collectors.toList());
    }

    public Account getAccountById(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public Account createOrUpdateAccount(AccountDTO dto) {
        Optional<Account> existing = accountRepository.findByAccountNumber(dto.getAccountNumber());

        Account account;
        if (existing.isPresent()) {
            account = existing.get();
            //Revisar que actualice la versión.
            account.setAccountNumber(dto.getAccountNumber());
            account.setOwnerName(dto.getOwnerName());
            account.setStatus(dto.getStatus());
            account.setBalance(dto.getBalance());
        }
        else {
            account = Account.builder()
                .accountNumber(dto.getAccountNumber())
                .ownerName(dto.getOwnerName())
                .balance(dto.getBalance())
                .status(AccountStatus.ACTIVE)
                .build();
        }
        

        Account saved = accountRepository.save(account);
        return saved;
    }

    public Account createOrUpdateAccount(Account account) {
        Account saved = accountRepository.save(account);
        return saved;
    }

    // 🔁 Conversión entidad <-> DTO
    public AccountDTO accountToDto(Account a) {
        return AccountDTO.builder()
                .accountNumber(a.getAccountNumber())
                .ownerName(a.getOwnerName())
                .balance(a.getBalance())
                .createdAt(a.getCreatedAt())
                .status(a.getStatus())
                .build();
    }

    public void sendAccountRequested(AccountRequestedEvent accountRequestedEvent) {
        kafkaAccountProducer.sendAccountRequested(accountRequestedEvent);
    }
}