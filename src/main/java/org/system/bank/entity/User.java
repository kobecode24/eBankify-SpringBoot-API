package org.system.bank.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.system.bank.enums.Role;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @NotBlank
    private String name;

    @Column(unique = true, nullable = false)
    @NotBlank
    @Email
    private String email;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_keycloak_roles",
            joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> keycloakRoles = new HashSet<>();

    @Column(name = "keycloak_id")
    private String keycloakId;

    // Method to update the role based on Keycloak roles
    public void updateRole(Role newRole) {
        this.role = newRole;
    }
}
