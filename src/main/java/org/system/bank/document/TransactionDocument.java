package org.system.bank.document;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;
import org.system.bank.enums.*;

import java.time.LocalDateTime;

@Document(indexName = "transactions")
@Setting(settingPath = "elasticsearch/transaction-settings.json")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDocument {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private Long transactionId;

    @Field(type = FieldType.Double)
    private Double amount;

    @Field(type = FieldType.Keyword)
    private TransactionType type;

    @Field(type = FieldType.Keyword)
    private TransactionStatus status;

    @Field(type = FieldType.Keyword)
    private Long sourceAccountId;

    @Field(type = FieldType.Keyword)
    private Long destinationAccountId;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "custom_analyzer"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword)
            }
    )
    private String sourceAccountHolder;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "custom_analyzer"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword)
            }
    )
    private String destinationAccountHolder;

    @Field(type = FieldType.Date, format = {}, pattern = "strict_date_time||strict_date_optional_time||epoch_millis")
    private LocalDateTime createdAt;
}