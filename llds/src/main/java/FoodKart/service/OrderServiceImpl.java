package FoodKart.service;

import FoodKart.exception.NotFoundException;
import FoodKart.model.MenuItem;
import FoodKart.model.Order;
import FoodKart.model.OrderItem;
import FoodKart.model.OrderStatus;
import FoodKart.model.Restaurant;
import FoodKart.repo.OrderRepository;
import FoodKart.repo.RestaurantRepository;
import FoodKart.service.strategy.RestaurantSelectionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final RestaurantRepository restaurantRepository;

    private final RestaurantSelectionStrategy strategy;

    @Override
    public Order placeOrder(String orderId, List<String> requestedItems, long timestamp) {

        List<Restaurant> eligibleRestaurants = restaurantRepository.findAll().stream()
                .filter(r -> canAcceptOrder(r, requestedItems)).collect(Collectors.toList());

        if (eligibleRestaurants.isEmpty()) {

            Order rejectedOrder = new Order(orderId, null, Collections.emptyList(), 0, OrderStatus.REJECTED, timestamp);

            orderRepository.save(rejectedOrder);

            return rejectedOrder;
        }

        Restaurant selectedRestaurant = strategy.selectRestaurant(eligibleRestaurants, requestedItems).orElseThrow();

        List<OrderItem> orderItems = new ArrayList<>();

        double totalPrice = 0;

        for (String item : requestedItems) {

            MenuItem menuItem = selectedRestaurant.getMenu().get(item);

            orderItems.add(new OrderItem(item, menuItem.getPrice(), 1));

            totalPrice += menuItem.getPrice();
        }

        Order order = new Order(orderId, selectedRestaurant.getId(), orderItems, totalPrice, OrderStatus.ACCEPTED, timestamp);

        selectedRestaurant.getActiveOrders().add(orderId);

        orderRepository.save(order);

        return order;
    }

    private boolean canAcceptOrder(Restaurant restaurant, List<String> items) {

        boolean allItemsPresent = items.stream().allMatch(restaurant.getMenu()::containsKey);

        boolean capacityAvailable = restaurant.getActiveOrders().size() < restaurant.getProcessingCapacity();

        return allItemsPresent && capacityAvailable;
    }

    @Override
    public void dispatchOrder(String orderId) {

        Order order = orderRepository.findById(orderId);

        if (order == null) {
            throw new NotFoundException("Order not found");
        }

        if (order.getStatus() != OrderStatus.ACCEPTED) {

            return;
        }

        Restaurant restaurant = restaurantRepository.findById(order.getRestaurantId());

        restaurant.getActiveOrders().remove(orderId);

        for (OrderItem item : order.getItems()) {

            restaurant.getServedItems().merge(item.getItemName(), item.getQuantity(), Integer::sum);
        }

        order.setStatus(OrderStatus.DISPATCHED);
    }

    @Override
    public List<Order> getDispatchedOrders() {

        return orderRepository.findAll().stream().filter(o -> o.getStatus() == OrderStatus.DISPATCHED).toList();
    }

    @Override
    public List<Order> getAllOrders() {

        return new ArrayList<>(orderRepository.findAll());
    }
}
