package com.example.demo.entities;

import com.example.demo.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;

// Account.java
@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    private Double balance;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}