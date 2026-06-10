package FoodKart.service;


import FoodKart.model.Order;

import java.util.List;

public interface OrderService {

    Order placeOrder(
            String orderId,
            List<String> items,
            long timestamp);

    void dispatchOrder(String orderId);

    List<Order> getDispatchedOrders();

    List<Order> getAllOrders();
}
