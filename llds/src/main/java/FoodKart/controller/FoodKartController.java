package FoodKart.controller;


import FoodKart.dto.CreateRestaurantRequest;
import FoodKart.dto.PlaceOrderRequest;
import FoodKart.dto.UpdatePriceRequest;
import FoodKart.model.MenuItem;
import FoodKart.model.Order;
import FoodKart.model.Restaurant;
import FoodKart.service.OrderService;
import FoodKart.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/foodkart")
@RequiredArgsConstructor
public class FoodKartController {

    private final RestaurantService restaurantService;

    private final OrderService orderService;

    @PostMapping("/restaurants")
    public String onboardRestaurant(@RequestBody CreateRestaurantRequest request) {

        Restaurant restaurant = new Restaurant();

        restaurant.setId(request.getId());

        restaurant.setName(request.getName());

        restaurant.setLocation(request.getLocation());

        restaurant.setProcessingCapacity(request.getCapacity());

        Map<String, MenuItem> menu = new HashMap<>();

        request.getMenu().forEach((item, price) -> menu.put(item, new MenuItem(item, price)));

        restaurant.setMenu(menu);

        restaurantService.onboardRestaurant(restaurant);

        return "Restaurant onboarded";
    }

    @PatchMapping("/restaurants/{restaurantId}/price")
    public String updatePrice(@PathVariable String restaurantId, @RequestBody UpdatePriceRequest request) {

        restaurantService.updatePrice(restaurantId, request.getItemName(), request.getPrice());

        return "Price updated";
    }

    @PostMapping("/orders")
    public Order placeOrder(@RequestBody PlaceOrderRequest request) {

        return orderService.placeOrder(request.getOrderId(), request.getItems(), System.currentTimeMillis());
    }

    @PostMapping("/orders/{orderId}/dispatch")
    public String dispatch(@PathVariable String orderId) {

        orderService.dispatchOrder(orderId);

        return "Order dispatched";
    }

    @GetMapping("/orders/dispatched")
    public Object dispatchedOrders() {

        return orderService.getDispatchedOrders();
    }
}
