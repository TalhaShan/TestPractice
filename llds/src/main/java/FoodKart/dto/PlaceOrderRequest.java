package FoodKart.dto;

import lombok.Data;

import java.util.List;

@Data
public class PlaceOrderRequest {

    private String orderId;

    private List<String> items;
}
