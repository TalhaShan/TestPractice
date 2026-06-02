package com.mine.built.it.controller;

import com.mine.built.it.dto.CreateOrderRequest;
import com.mine.built.it.entity.CustomerOrder;
import com.mine.built.it.service.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<CustomerOrder> createOrder(@RequestBody CreateOrderRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder2(request));
    }

    @PostMapping("/create")
    public ResponseEntity<CustomerOrder> createOrder(@RequestHeader("Idempotency-Key") String idemKey,@RequestBody CreateOrderRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request,idemKey));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CustomerOrder>> getCustomerOrders(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getCustomerOrders(customerId));
    }
}
