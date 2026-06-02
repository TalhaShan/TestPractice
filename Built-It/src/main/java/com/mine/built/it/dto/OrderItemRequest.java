package com.mine.built.it.dto;

import lombok.Data;

@Data
public class OrderItemRequest {

    private Long productId;

    private Integer quantity;
}
