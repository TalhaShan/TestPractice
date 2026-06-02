package com.mine.built.it.service;

import com.google.common.hash.Hashing;
import com.mine.built.it.dto.CreateOrderRequest;
import com.mine.built.it.dto.OrderItemRequest;
import com.mine.built.it.entity.Customer;
import com.mine.built.it.entity.CustomerOrder;
import com.mine.built.it.entity.OrderItem;
import com.mine.built.it.entity.Product;
import com.mine.built.it.enums.OrderStatus;
import com.mine.built.it.idempotencychapter.IdempotencyKeyEntity;
import com.mine.built.it.idempotencychapter.IdempotencyResult;
import com.mine.built.it.idempotencychapter.IdempotencyService;
import com.mine.built.it.repository.CustomerRepository;
import com.mine.built.it.repository.OrderRepository;
import com.mine.built.it.repository.ProductRepository;
import com.mine.built.it.service.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.json.JsonParseException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    private final IdempotencyService idempotencyService;

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "products", allEntries = true)
            }
    )
    public CustomerOrder createOrder2(CreateOrderRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        CustomerOrder order = new CustomerOrder();

        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.CREATED);

        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {

            /// -------- Pessimistic Locking------- ////
//            Product product = productRepository.findByIdForUpdate(itemRequest.getProductId())
//                    .orElseThrow(() -> new RuntimeException("Product not found"));

//            product.setAvailableQuantity(
//                    product.getAvailableQuantity() - itemRequest.getQuantity()
//            );
            /// -------- Pessimistic Locking END ------- ////
            //or//
            /// -------- In more high scale system you can do Atomic Locking ------- ////
            Product product = productRepository.findDetailedProduct(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            int updated = productRepository.decreaseStock(
                    itemRequest.getProductId(),
                    itemRequest.getQuantity()
            );

            if (updated == 0) {
                throw new RuntimeException("Insufficient stock");
            }
            /// -------- Atomic Locking END ------- ////

//            if (product.getAvailableQuantity() < itemRequest.getQuantity()) {
//                throw new RuntimeException(
//                        "Insufficient stock for product: " + product.getName()
//                );
//            }


            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setPrice(product.getPrice());

            items.add(item);
        }

        order.setItems(items);

        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerOrder> getCustomerOrders(Long customerId) {

        return orderRepository.findByCustomerId(customerId);
    }

    //Lets try Idempotency
    // ← single transaction wraps EVERYTHING: idem checks + business logic
    @Transactional
    public CustomerOrder createOrder(CreateOrderRequest request, String idemKey) {

        String requestHash;

        try {
            requestHash = Hashing.sha256()
                    .hashString(
                            objectMapper.writeValueAsString(request),
                            StandardCharsets.UTF_8
                    )
                    .toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash request", e);
        }

        IdempotencyResult idemResult =
                idempotencyService.handleRequest(idemKey, requestHash);

        // Duplicate request → return cached response
        if (idemResult.isDuplicate()) {

            try {
                return objectMapper.readValue(
                        idemResult.getCachedJson(),
                        CustomerOrder.class
                );
            } catch (Exception e) {
                throw new RuntimeException(
                        "Failed to deserialize cached order",
                        e
                );
            }
        }

        IdempotencyKeyEntity idemEntity = idemResult.getEntity();

        try {

            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new RuntimeException(
                            "Customer not found: " + request.getCustomerId()));

            CustomerOrder order = new CustomerOrder();
            order.setCustomer(customer);
            order.setOrderDate(LocalDateTime.now());
            order.setOrderStatus(OrderStatus.CREATED);

            List<OrderItem> items = new ArrayList<>();

            for (OrderItemRequest itemRequest : request.getItems()) {

                Product product = productRepository
                        .findByIdForUpdate(itemRequest.getProductId())
                        .orElseThrow(() -> new RuntimeException(
                                "Product not found: " + itemRequest.getProductId()));

                if (product.getAvailableQuantity() < itemRequest.getQuantity()) {
                    throw new RuntimeException(
                            "Insufficient stock for: " + product.getName());
                }

                product.setAvailableQuantity(
                        product.getAvailableQuantity()
                                - itemRequest.getQuantity());

                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setProduct(product);
                item.setQuantity(itemRequest.getQuantity());
                item.setPrice(product.getPrice());

                items.add(item);
            }

            order.setItems(items);
            CustomerOrder savedOrder = orderRepository.save(order);
            String responseJson =
                    objectMapper.writeValueAsString(savedOrder);
            idempotencyService.markCompleted(
                    idemEntity,
                    responseJson
            );
            return savedOrder;
        } catch (Exception ex) {

            idempotencyService.markFailed(idemEntity);

            throw ex;
        }
    }
}

