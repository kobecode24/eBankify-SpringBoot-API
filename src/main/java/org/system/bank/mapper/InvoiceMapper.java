package org.system.bank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.system.bank.dto.request.InvoiceCreationRequest;
import org.system.bank.dto.response.InvoiceResponse;
import org.system.bank.entity.Invoice;

import java.util.List;

@Mapper(componentModel = "spring" , unmappedTargetPolicy =  ReportingPolicy.IGNORE)
public interface InvoiceMapper {

    @Mapping(target = "invoiceId", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "user.userId", source = "userId")
    Invoice toEntity(InvoiceCreationRequest request);

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "userName", source = "user.name")
    InvoiceResponse toResponse(Invoice invoice);

    List<InvoiceResponse> toResponseList(List<Invoice> invoices);
}
