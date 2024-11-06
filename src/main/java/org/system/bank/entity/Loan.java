package org.system.bank.entity;

import jakarta.persistence.*;
import lombok.*;
import org.system.bank.enums.LoanStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long loanId;

    @Column(nullable = false)
    private Double principal;

    @Column(nullable = false)
    private Double interestRate;

    @Column(nullable = false)
    private Integer termMonths;

    @Column(nullable = false)
    private Double monthlyPayment;

    private Double remainingAmount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @Column(columnDefinition = "TEXT")
    private String guarantees;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = LoanStatus.PENDING;
        }
        if (remainingAmount == null) {
            remainingAmount = principal;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}