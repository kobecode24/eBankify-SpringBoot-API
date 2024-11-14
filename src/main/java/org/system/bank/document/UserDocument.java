package org.system.bank.document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;
import org.system.bank.enums.Role;

import java.util.List;

@Document(indexName = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDocument {
    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long userId;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "custom_analyzer"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword, normalizer = "lowercase_normalizer")
            }
    )
    private String name;

    @Field(type = FieldType.Keyword)
    private String email;

    @Field(type = FieldType.Integer)
    private Integer age;

    @Field(type = FieldType.Double)
    private Double monthlyIncome;

    @Field(type = FieldType.Integer)
    private Integer creditScore;

    @Field(type = FieldType.Keyword)
    private Role role;

    @Field(type = FieldType.Keyword)
    private List<String> accountIds;
}
