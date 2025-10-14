package com.bank.accountService.model.account;

import java.util.UUID;

public record AccountRequestedEvent(
    UUID accountId,
    String accountNumber,
    AccountStatus status,
    UUID version
) {}