package com.mine.built.it.unit;


import com.mine.built.it.dto.CreateOrderRequest;
import com.mine.built.it.dto.OrderItemRequest;

import java.util.List;

public class TestData {

    public static CreateOrderRequest orderRequest() {

        OrderItemRequest item = new OrderItemRequest();

        item.setProductId(1L);
        item.setQuantity(1);

        CreateOrderRequest request = new CreateOrderRequest();

        request.setCustomerId(1L);
        request.setItems(List.of(item));

        return request;
    }
}

