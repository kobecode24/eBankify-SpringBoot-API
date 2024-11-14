package org.system.bank.document;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;
import org.system.bank.enums.*;

import java.time.LocalDate;

@Document(indexName = "loans")
@Setting(settingPath = "elasticsearch/loan-settings.json")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanDocument {
    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long loanId;

    @Field(type = FieldType.Double)
    private Double principal;

    @Field(type = FieldType.Double)
    private Double interestRate;

    @Field(type = FieldType.Integer)
    private Integer termMonths;

    @Field(type = FieldType.Double)
    private Double monthlyPayment;

    @Field(type = FieldType.Double)
    private Double remainingAmount;

    @Field(type = FieldType.Keyword)
    private LoanStatus status;

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
    private LocalDate startDate;

    @Field(type = FieldType.Date)
    private LocalDate endDate;

    @Field(type = FieldType.Text)
    private String guarantees;
}