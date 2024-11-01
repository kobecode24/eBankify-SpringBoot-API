package org.system.bank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.system.bank.dto.request.TransactionRequest;
import org.system.bank.dto.response.TransactionResponse;
import org.system.bank.entity.Transaction;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "sourceAccount.accountId", source = "sourceAccountId")
    @Mapping(target = "destinationAccount.accountId", source = "destinationAccountId")
    Transaction toEntity(TransactionRequest request);

    @Mapping(target = "sourceAccountId", source = "sourceAccount.accountId")
    @Mapping(target = "destinationAccountId", source = "destinationAccount.accountId")
    @Mapping(target = "fee", expression = "java(calculateTransactionFee(transaction))")
    TransactionResponse toResponse(Transaction transaction);

    List<TransactionResponse> toResponseList(List<Transaction> transactions);

    default Double calculateTransactionFee(Transaction transaction) {
        return switch (transaction.getType()) {
            case INSTANT -> transaction.getAmount() * 0.005; // 0.5%
            case STANDARD -> transaction.getAmount() * 0.001; // 0.1%
        };
    }
}