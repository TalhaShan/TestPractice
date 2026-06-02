package com.mine.built.it.unit;


import com.mine.built.it.dto.CreateOrderRequest;
import com.mine.built.it.dto.OrderItemRequest;
import com.mine.built.it.entity.Customer;
import com.mine.built.it.entity.CustomerOrder;
import com.mine.built.it.entity.Product;
import com.mine.built.it.idempotencychapter.IdempotencyService;
import com.mine.built.it.repository.CustomerRepository;
import com.mine.built.it.repository.OrderRepository;
import com.mine.built.it.repository.ProductRepository;
import com.mine.built.it.service.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private IdempotencyService idempotencyService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void shouldThrowWhenInsufficientStock() {

        Customer customer = new Customer();
        customer.setId(1L);

        Product product = new Product();
        product.setId(1L);

        CreateOrderRequest request = new CreateOrderRequest();

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(100);

        request.setCustomerId(1L);
        request.setItems(List.of(item));

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(productRepository.findDetailedProduct(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.decreaseStock(1L, 100))
                .thenReturn(0);

        assertThatThrownBy(() ->
                orderService.createOrder2(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    void shouldCreateOrderSuccessfully() {

        Customer customer = new Customer();
        customer.setId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setPrice(BigDecimal.TEN);

        CreateOrderRequest request = new CreateOrderRequest();

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(2);

        request.setCustomerId(1L);
        request.setItems(List.of(item));

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(productRepository.findDetailedProduct(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.decreaseStock(1L, 2))
                .thenReturn(1);

        when(orderRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        CustomerOrder order = orderService.createOrder2(request);

        assertThat(order.getItems()).hasSize(1);
        verify(orderRepository).save(any());
    }
}
