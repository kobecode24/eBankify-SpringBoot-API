package org.system.bank.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.system.bank.enums.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

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
    private Role role;
}
