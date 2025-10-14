package com.bank.transactionService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.transactionService.model.account.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findById(UUID accountId);

    Optional<Account> findByAccountNumber(String accountNumber);
}