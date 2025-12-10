package com.bank.transactionservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.bank.transactionservice.event.consumer.AccountProcessedEvent;
import com.bank.transactionservice.model.account.Account;
import com.bank.transactionservice.model.account.AccountStatus;
import com.bank.transactionservice.repository.AccountRepository;

import java.util.Optional;
import com.bank.transactionservice.exception.AccountNotFound;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account getByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFound("Account with number " + accountNumber + " not found"));
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

    public Account createAccountFromConsumer(AccountProcessedEvent accountProcessedEvent) {
        Account account = newAccount(accountProcessedEvent);
        return accountRepository.save(account);
    }

    public Account updateAccountFromConsumer(AccountProcessedEvent accountProcessedEvent) {
        Optional<Account> existing = accountRepository.findById(accountProcessedEvent.accountId());
        if (existing.isEmpty()) {
            throw new AccountNotFound("Account not found for update: " + accountProcessedEvent.accountId());
        }
        Account account = updateAccount(accountProcessedEvent, existing.get());
        return accountRepository.save(account);
    }

    
    public Account updateAccount(AccountProcessedEvent accountProcessedEvent, Account account) {
        account.setAccountNumber(accountProcessedEvent.accountNumber().toString());
        account.setStatus(AccountStatus.valueOf(accountProcessedEvent.status().toString()));
        account.setVersionId(accountProcessedEvent.version());
        return account;
    }

    public Account newAccount(AccountProcessedEvent accountProcessedEvent) {
        Account account = Account.builder()
                    .accountNumber(accountProcessedEvent.accountNumber().toString())
                    .status(AccountStatus.valueOf(accountProcessedEvent.status().toString()))
                    .versionId(accountProcessedEvent.version())
                    .id(accountProcessedEvent.accountId())
                    .build();
        return account;
    }
}