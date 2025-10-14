package com.bank.transactionService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.bank.transactionService.model.account.Account;
import com.bank.transactionService.model.account.AccountDTO;
import com.bank.transactionService.model.account.AccountProcessedEvent;
import com.bank.transactionService.repository.AccountRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::accountToDto)
                .collect(Collectors.toList());
    }

    /**
     * Recupera una cuenta por su accountNumber.
     */
    public Account getByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }


    /**
     * Crea o actualiza la copia local de la cuenta.
     * Seguramente tenga que cambiar esto para que me haga con los datos que le traigo del kafka y no con los del dto
     */
    @Transactional
    public AccountDTO saveOrUpdateDTO(AccountDTO dto) {
        Optional<Account> existing = accountRepository.findByAccountNumber(dto.getAccountNumber());

        Account account;
        if (existing.isPresent()) {
            account = existing.get();
            //Revisar que actualice la versión.
            account.setStatus(dto.getStatus());
        } else {
            account = Account.builder()
                    .accountNumber(dto.getAccountNumber())
                    .status(dto.getStatus())
                    .build();
        }

        Account saved = accountRepository.save(account);
        return accountToDto(saved);
    }

    /**
     * Crea o actualiza la copia local de la cuenta.
     * Seguramente tenga que cambiar esto para que me haga con los datos que le traigo del kafka y no con los del dto
     */
    @Transactional
    public Account saveOrUpdateFromConsumer(AccountProcessedEvent accountProcessedEvent) {
        Optional<Account> existing = accountRepository.findById(accountProcessedEvent.accountId());

        Account account;
        if (existing.isPresent()) {
            account = existing.get();
            //Revisar que actualice la versión.
            account.setAccountNumber(accountProcessedEvent.accountNumber());
            account.setStatus(accountProcessedEvent.status());
            account.setVersionId(accountProcessedEvent.version());
        } else {
            account = Account.builder()
                    .accountNumber(accountProcessedEvent.accountNumber())
                    .status(accountProcessedEvent.status())
                    .versionId(accountProcessedEvent.version())
                    .id(accountProcessedEvent.accountId())
                    .build();
        }

        Account saved = accountRepository.save(account);
        return saved;
    }

    public AccountDTO accountToDto(Account a) {
        return AccountDTO.builder()
                .accountNumber(a.getAccountNumber())
                .status(a.getStatus())
                .build();
    }
}