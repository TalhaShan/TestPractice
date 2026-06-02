package com.mine.built.it.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    private String name;

    private String sku;

    private BigDecimal price;

    private Integer availableQuantity;

    private Long categoryId;

    private Long supplierId;
}
