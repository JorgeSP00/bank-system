package com.bank.accountservice.model.outbox;

public enum OutboxStatus {
    PENDING,
    SENT,
    FAILED
}