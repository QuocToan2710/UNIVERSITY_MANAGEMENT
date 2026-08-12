package com.toan.university_management.dto.request.masterdata;

import com.toan.university_management.enums.ComboType;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetComboDataSourceInput {
    ComboType type;
    String cascader;
    String codeSystem;
    Boolean isCodeIsId;
}
