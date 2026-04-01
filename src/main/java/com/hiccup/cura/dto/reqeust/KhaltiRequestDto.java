package com.hiccup.cura.dto.reqeust;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class KhaltiRequestDto {
    private BigDecimal amount;
    private String returnUrl;
    private String purchaseOrderId;
    private String purchaseOrderName;
    private String websiteUrl;
}
