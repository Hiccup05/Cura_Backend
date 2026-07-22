package com.hiccup.cura.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class KhaltiRequestDto {
    private BigDecimal amount;
    @JsonProperty("return_url")
    private String returnUrl;
    @JsonProperty("purchase_order_id")
    private Long purchaseOrderId;
    @JsonProperty("purchase_order_name")
    private String purchaseOrderName;
    @JsonProperty("website_url")
    private String websiteUrl;
}
