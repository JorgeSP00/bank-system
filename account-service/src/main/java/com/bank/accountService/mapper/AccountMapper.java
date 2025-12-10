package com.bank.accountservice.mapper;


import org.springframework.stereotype.Component;

import com.bank.accountservice.dto.response.AccountResponseDTO;
import com.bank.accountservice.dto.message.AccountRequestedMessage;
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

    public AccountRequestedMessage fromEntityToMessage(Account account) {
        return AccountRequestedMessage.builder()
                .accountId(account.getId())
                .accountNumber(account.getAccountNumber())
                .status(account.getStatus().toString())
                .version(account.getVersionId())
                .build();
    }
}
