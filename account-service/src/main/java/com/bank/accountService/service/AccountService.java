package com.bank.accountservice.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.accountservice.dto.request.AccountRequestDTO;
import com.bank.accountservice.dto.response.AccountResponseDTO;
import com.bank.accountservice.event.producer.AccountRequestedEvent;
import com.bank.accountservice.exception.AccountAlreadyExists;
import com.bank.accountservice.exception.AccountNotFound;
import com.bank.accountservice.kafka.producer.KafkaAccountProducer;
import com.bank.accountservice.mapper.AccountMapper;
import com.bank.accountservice.model.account.Account;
import com.bank.accountservice.repository.AccountRepository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final KafkaAccountProducer kafkaAccountProducer;

    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper;

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
            sendAccountRequested(accountMapper.fromEntityToMessage(saved));
            return accountMapper.fromEntityToResponse(saved);
        } else {
            throw new AccountNotFound("Account with ID " + id + " not found");
        }
    }

    @Transactional
    public Account updateAccount(Account account) {
        return accountRepository.save(account);
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
            sendAccountRequested(accountMapper.fromEntityToMessage(saved));
            return accountMapper.fromEntityToResponse(saved);
        }
    }

    private void sendAccountRequested(AccountRequestedEvent accountRequestedEvent) {
        kafkaAccountProducer.sendAccountRequested(accountRequestedEvent);
    }
}