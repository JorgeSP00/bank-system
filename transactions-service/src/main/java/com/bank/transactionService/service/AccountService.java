package com.bank.transactionService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.bank.transactionService.model.Account;
import com.bank.transactionService.model.DTO.AccountDTO;
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
    public AccountDTO getByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(this::accountToDto)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }


    /**
     * Crea o actualiza la copia local de la cuenta.
     * Seguramente tenga que cambiar esto para que me haga con los datos que le traigo del kafka y no con los del dto
     */
    @Transactional
    public AccountDTO saveOrUpdate(AccountDTO dto) {
        Optional<Account> existing = accountRepository.findByAccountNumber(dto.getAccountNumber());

        Account account;
        if (existing.isPresent()) {
            account = existing.get();
            //Revisar que actualice la versión.
            account.setOwnerName(dto.getOwnerName());
            account.setStatus(dto.getStatus());
        } else {
            account = Account.builder()
                    .accountNumber(dto.getAccountNumber())
                    .ownerName(dto.getOwnerName())
                    .status(dto.getStatus())
                    .build();
        }

        Account saved = accountRepository.save(account);
        return accountToDto(saved);
    }

    private AccountDTO accountToDto(Account a) {
        return AccountDTO.builder()
                .accountNumber(a.getAccountNumber())
                .ownerName(a.getOwnerName())
                .status(a.getStatus())
                .build();
    }
}