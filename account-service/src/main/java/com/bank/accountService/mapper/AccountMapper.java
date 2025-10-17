package com.bank.accountservice.mapper;


import org.springframework.stereotype.Component;

import com.bank.accountservice.dto.response.AccountResponseDTO;
import com.bank.accountservice.event.producer.AccountRequestedEvent;
import com.bank.accountservice.model.account.Account;
@Component
public class AccountMapper {


    public AccountResponseDTO fromEntityToResponse(Account a) {
        return AccountResponseDTO.builder()
                .accountNumber(a.getAccountNumber())
                .ownerName(a.getOwnerName())
                .balance(a.getBalance())
                .status(a.getStatus())
                .createdAt(a.getCreatedAt())
                .build();
    }

    public AccountRequestedEvent fromEntityToMessage(Account account) {
        return new AccountRequestedEvent(account.getId(), account.getAccountNumber(), account.getStatus(), account.getVersionId());
    }
}
