package FoodKart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private String orderId;

    private String restaurantId;

    private List<OrderItem> items;

    private double totalAmount;

    private OrderStatus status;

    private long timestamp;
}
