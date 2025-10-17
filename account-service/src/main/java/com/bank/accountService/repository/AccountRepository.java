package com.bank.accountservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.accountservice.model.account.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByAccountNumber(String accountNumber);
}