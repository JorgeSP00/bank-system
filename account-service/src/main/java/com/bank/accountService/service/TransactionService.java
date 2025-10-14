package com.bank.accountService.service;

import org.springframework.stereotype.Service;

import com.bank.accountService.model.account.Account;
import com.bank.accountService.model.account.AccountStatus;
import com.bank.accountService.model.transaction.TransactionCompletedRequestedEvent;
import com.bank.accountService.model.transaction.TransactionProcessedEvent;
import com.bank.accountService.model.transaction.TransactionStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final AccountService accountService;
    private final KafkaTransactionCompletedProducer kafkaTransactionProducer;

    public void doTransaction(TransactionProcessedEvent transactionProcessedEvent) {
        TransactionStatus transactionState;
        Account fromAccount = accountService.getAccountById(transactionProcessedEvent.fromAccountId());
        Account toAccount = accountService.getAccountById(transactionProcessedEvent.toAccountId());
        
        if (checkAccounts(transactionProcessedEvent, fromAccount, toAccount) && updateAccounts(transactionProcessedEvent, fromAccount, toAccount)) {
            transactionState = TransactionStatus.CORRECT;
        } else {
            transactionState = TransactionStatus.INCORRECT;
        }
        completeTransaction(transactionProcessedEvent, transactionState);
    }

    private boolean checkAccounts(TransactionProcessedEvent transactionProcessedEvent, Account fromAccount, Account toAccount) {
        if (fromAccount.getVersionId().equals(transactionProcessedEvent.fromAccountVersionId()) && fromAccount.getStatus().equals(AccountStatus.ACTIVE) && fromAccount.getBalance().compareTo(transactionProcessedEvent.amount()) != -1 &&
            toAccount.getVersionId().equals(transactionProcessedEvent.toAccountVersionId()) && toAccount.getStatus().equals(AccountStatus.ACTIVE)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean updateAccounts (TransactionProcessedEvent transactionProcessedEvent, Account fromAccount, Account toAccount) {
        fromAccount.setBalance(fromAccount.getBalance().subtract(transactionProcessedEvent.amount()));
        toAccount.setBalance(toAccount.getBalance().add(transactionProcessedEvent.amount()));

        accountService.createOrUpdateAccount(fromAccount);
        accountService.createOrUpdateAccount(toAccount);
        return true;
    }

    private void completeTransaction(TransactionProcessedEvent transactionProcessedEvent, TransactionStatus transactionState) {
        kafkaTransactionProducer.sendTransactionConfirmedRequested(new TransactionCompletedRequestedEvent(transactionProcessedEvent.transactionId(), transactionState, "null"));
    }
}
