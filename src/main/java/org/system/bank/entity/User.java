package org.system.bank.entity;

import jakarta.persistence.*;
import lombok.*;
import org.system.bank.enums.Role;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private Integer age;
    private Double monthlyIncome;
    private Integer creditScore;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Account> accounts;
}
