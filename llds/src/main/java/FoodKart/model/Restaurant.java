package FoodKart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    private String id;

    private String name;

    private String location;

    private int processingCapacity;

    private Map<String, MenuItem> menu = new HashMap<>();

    private Set<String> activeOrders = new HashSet<>();

    private Map<String,Integer> servedItems = new HashMap<>();
}
