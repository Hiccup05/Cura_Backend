package com.hiccup.cura.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductDto {
    private String serviceName;
    private String description;
    private Double price;
}
