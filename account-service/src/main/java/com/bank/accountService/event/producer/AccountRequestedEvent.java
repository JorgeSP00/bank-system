package com.bank.accountservice.event.producer;

import java.util.UUID;

import com.bank.accountservice.model.account.AccountStatus;

public record AccountRequestedEvent(
    UUID accountId,
    String accountNumber,
    AccountStatus status,
    UUID version
) {}