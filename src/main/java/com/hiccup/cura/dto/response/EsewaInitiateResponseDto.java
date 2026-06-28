package com.hiccup.cura.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hiccup.cura.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EsewaInitiateResponseDto {
    private String status;

    private String signature;

    @JsonProperty("transaction_code")
    private String transactionCode;

    @JsonProperty("total_amount")
    private String totalAmount;

    @JsonProperty("transaction_uuid")
    private String transactionUuid;

    @JsonProperty("product_code")
    private String productCode;

    @JsonProperty("signed_field_names")
    private String signedFieldNames;
}
