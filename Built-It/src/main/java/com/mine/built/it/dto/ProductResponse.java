package com.mine.built.it.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class ProductResponse {

    private Long id;

    private String name;

    private String sku;

    private BigDecimal price;

    private Integer availableQuantity;

    private String categoryName;

    private String supplierName;
}
