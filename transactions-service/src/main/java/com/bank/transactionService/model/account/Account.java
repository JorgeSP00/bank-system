package com.bank.transactionService.model.account;

import java.util.UUID;

import com.bank.transactionService.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@Table(name = "account")
@SuperBuilder
@NoArgsConstructor
public class Account extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(nullable = false)
    private UUID versionId;
}
