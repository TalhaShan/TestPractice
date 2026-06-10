package FoodKart.dto;

import lombok.Data;

import java.util.Map;

@Data
public class CreateRestaurantRequest {

    private String id;

    private String name;

    private String location;

    private int capacity;

    private Map<String, Double> menu;
}
