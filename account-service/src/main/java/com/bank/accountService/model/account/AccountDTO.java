package com.bank.accountService.model.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    private String accountNumber;
    private String ownerName;
    private BigDecimal balance;
    private AccountStatus status;
    private LocalDateTime createdAt;
}