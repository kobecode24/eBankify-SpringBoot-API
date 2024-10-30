package org.system.bank.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.system.bank.enums.LoanStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponse {
    private Long loanId;
    private Double principal;
    private Double interestRate;
    private Integer termMonths;
    private Double monthlyPayment;
    private Double remainingAmount;
    private LoanStatus status;
    private Long userId;
    private String userName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String guarantees;
}
