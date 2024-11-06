package org.system.bank.entity;

import lombok.*;
import jakarta.persistence.*;
import org.system.bank.enums.AccountStatus;

import java.util.List;


@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long accountId;

    private Double balance;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "sourceAccount")
    private List<Transaction> outgoingTransactions;

    @OneToMany(mappedBy = "destinationAccount")
    private List<Transaction> incomingTransactions;
}