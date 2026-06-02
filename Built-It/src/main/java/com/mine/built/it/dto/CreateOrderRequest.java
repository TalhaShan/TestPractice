package com.mine.built.it.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    private Long customerId;
    private List<OrderItemRequest> items;
}
