package com.bank.transactionservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.bank.transactionservice.event.AccountProcessedEvent;
import com.bank.transactionservice.model.account.Account;
import com.bank.transactionservice.repository.AccountRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account getByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public Account saveOrUpdateFromConsumer(AccountProcessedEvent accountProcessedEvent) {
        Optional<Account> existing = accountRepository.findById(accountProcessedEvent.accountId());

        Account account;
        if (existing.isPresent()) {
            account = updateAccount(accountProcessedEvent, existing.get());
        } else {
            account = newAccount(accountProcessedEvent);
        }
        return accountRepository.save(account);
    }

    
    public Account updateAccount(AccountProcessedEvent accountProcessedEvent, Account account) {
        account.setAccountNumber(accountProcessedEvent.accountNumber());
        account.setStatus(accountProcessedEvent.status());
        account.setVersionId(accountProcessedEvent.version());
        return account;
    }

    public Account newAccount(AccountProcessedEvent accountProcessedEvent) {
        Account account = Account.builder()
                    .accountNumber(accountProcessedEvent.accountNumber())
                    .status(accountProcessedEvent.status())
                    .versionId(accountProcessedEvent.version())
                    .id(accountProcessedEvent.accountId())
                    .build();
        return account;
    }
}