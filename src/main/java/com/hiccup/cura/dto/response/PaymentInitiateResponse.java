package com.hiccup.cura.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInitiateResponse {
    private String url;
    private String methodType;
    private Map<String, String> fields;
}
