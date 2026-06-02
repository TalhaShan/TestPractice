package com.mine.built.it.service.interfaces;

import com.mine.built.it.dto.CreateOrderRequest;
import com.mine.built.it.entity.CustomerOrder;

import java.util.List;

public interface OrderService {

    CustomerOrder createOrder(CreateOrderRequest request,String idemKey);

    CustomerOrder createOrder2(CreateOrderRequest request);

    List<CustomerOrder> getCustomerOrders(Long customerId);
}
