package org.system.bank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.system.bank.dto.request.LoanApplicationRequest;
import org.system.bank.dto.response.LoanResponse;
import org.system.bank.entity.Loan;

import java.util.List;

@Mapper(componentModel = "spring" , unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanMapper {

    @Mapping(target = "loanId", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "remainingAmount", source = "principal")
    @Mapping(target = "user.userId", source = "userId")
    @Mapping(target = "interestRate", constant = "10.0") // Default interest rate
    Loan toEntity(LoanApplicationRequest request);

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "userName", source = "user.name")
    LoanResponse toResponse(Loan loan);

    List<LoanResponse> toResponseList(List<Loan> loans);
}
