package com.bank.accountservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import com.bank.accountservice.model.account.AccountStatus;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestDTO {
    private String accountNumber;
    private String ownerName;
    private BigDecimal balance;
    private AccountStatus status;
}