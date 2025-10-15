package com.bank.accountservice.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.bank.accountservice.dto.request.AccountRequestDTO;
import com.bank.accountservice.dto.response.AccountResponseDTO;
import com.bank.accountservice.event.AccountRequestedEvent;
import com.bank.accountservice.kafka.producer.KafkaAccountProducer;
import com.bank.accountservice.mapper.AccountMapper;
import com.bank.accountservice.model.account.Account;
import com.bank.accountservice.model.account.AccountStatus;
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
        return accountMapper.fromEntityToResponse(getAccountById(id));
    }

    public Account getAccountById(UUID id) {
        Account a = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return a;
    }

    public AccountResponseDTO createOrUpdateAccountResponseDTO(AccountRequestDTO dto) {
        Account a = createOrUpdateAccount(dto);
        sendAccountRequested(accountMapper.fromEntityToMessage(a));
        return accountMapper.fromEntityToResponse(createOrUpdateAccount(dto));
    }

    private Account createOrUpdateAccount(AccountRequestDTO dto) {
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

    private void sendAccountRequested(AccountRequestedEvent accountRequestedEvent) {
        kafkaAccountProducer.sendAccountRequested(accountRequestedEvent);
    }
}