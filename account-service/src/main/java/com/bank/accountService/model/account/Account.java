package com.bank.accountservice.model.account;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

import com.bank.accountservice.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private BigDecimal balance;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;    

    @Column(nullable = false)
    private UUID versionId;

    @PrePersist
    public void prePersist() {
        versionId = UUID.randomUUID();
    }

    @PreUpdate
    public void preUpdate() {
        versionId = UUID.randomUUID();
    }
}