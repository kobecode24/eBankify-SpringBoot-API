package org.system.bank.document;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;
import org.system.bank.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(indexName = "invoices")
@Setting(settingPath = "elasticsearch/invoice-settings.json")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDocument {
    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long invoiceId;

    @Field(type = FieldType.Double)
    private Double amountDue;

    @Field(type = FieldType.Date)
    private LocalDate dueDate;

    @Field(type = FieldType.Date)
    private LocalDate paidDate;

    @Field(type = FieldType.Keyword)
    private InvoiceStatus status;

    @Field(type = FieldType.Long)
    private Long userId;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "custom_analyzer"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword)
            }
    )
    private String userName;

    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;
}
