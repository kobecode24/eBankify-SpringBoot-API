package org.system.bank.document;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;
import org.system.bank.enums.*;


@Document(indexName = "accounts")
@Setting(settingPath = "elasticsearch/account-settings.json")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDocument {
    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long accountId;

    @Field(type = FieldType.Double)
    private Double balance;

    @Field(type = FieldType.Keyword)
    private AccountStatus status;

    @Field(type = FieldType.Long)
    private Long userId;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "custom_analyzer"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword, normalizer = "lowercase_normalizer")
            }
    )
    private String userName;
}
