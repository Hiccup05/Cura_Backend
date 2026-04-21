package com.hiccup.cura.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KhaltiLookupResponseDto {
    private String pidx;
    @JsonProperty("transaction_id")
    private String transactionId;
    private String status;
}
