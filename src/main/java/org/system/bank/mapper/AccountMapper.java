package org.system.bank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.system.bank.dto.request.AccountCreationRequest;
import org.system.bank.dto.response.AccountResponse;
import org.system.bank.entity.Account;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "balance", source = "initialDeposit")
    @Mapping(target = "user.userId", source = "userId")
    Account toEntity(AccountCreationRequest request);

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "userName", source = "user.name")
    AccountResponse toResponse(Account account);

    List<AccountResponse> toResponseList(List<Account> accounts);
}
