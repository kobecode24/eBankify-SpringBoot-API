package com.example.demo.entities;

import com.example.demo.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    @NotBlank
    @Email
    private String email;

    @Column(nullable = false)
    private String name;

    @NotBlank
    private String password;

    @NotNull
    @Min(18)
    private Integer age;

    @NotNull
    @Positive
    private Double monthlyIncome;

    @NotNull
    @Min(300)
    @Max(850)
    private Integer creditScore;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Account> accounts = new ArrayList<>();
}