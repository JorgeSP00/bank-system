package com.bank.accountService.service;

import com.bank.accountService.model.DTO.AccountDTO;
import com.bank.accountService.model.Account;
import com.bank.accountService.model.AccountStatus;
import com.bank.accountService.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public List<AccountDTO> findAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AccountDTO getAccountById(UUID id) {
        return accountRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public AccountDTO createOrUpdateAccount(AccountDTO dto) {
        Optional<Account> existing = accountRepository.findByAccountNumber(dto.getAccountNumber());

        Account account;
        if (existing.isPresent()) {
            account = existing.get();
            //Revisar que actualice la versión.
            account.setOwnerName(dto.getOwnerName());
            account.setStatus(dto.getStatus());
        }
        else {
            account = Account.builder()
                .accountNumber(dto.getAccountNumber())
                .ownerName(dto.getOwnerName())
                .balance(new BigDecimal(0.0))
                .status(AccountStatus.ACTIVE)
                .build();
        }
        

        Account saved = accountRepository.save(account);
        return toDto(saved);
    }

    // 🔁 Conversión entidad <-> DTO
    private AccountDTO toDto(Account a) {
        return AccountDTO.builder()
                .accountNumber(a.getAccountNumber())
                .balance(a.getBalance())
                .createdAt(a.getCreatedAt())
                .build();
    }
}