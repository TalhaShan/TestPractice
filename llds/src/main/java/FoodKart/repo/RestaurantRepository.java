package FoodKart.repo;

import FoodKart.model.Restaurant;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class RestaurantRepository {

    private final Map<String, Restaurant> restaurants =
            new ConcurrentHashMap<>();

    public void save(Restaurant restaurant) {

        restaurants.put(
                restaurant.getId(),
                restaurant
        );
    }

    public Restaurant findById(String id) {

        return restaurants.get(id);
    }

    public Collection<Restaurant> findAll() {

        return restaurants.values();
    }
}
