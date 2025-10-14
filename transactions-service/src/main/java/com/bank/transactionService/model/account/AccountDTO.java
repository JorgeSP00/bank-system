package com.bank.transactionService.model.account;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    private String accountNumber;
    private AccountStatus status;
}