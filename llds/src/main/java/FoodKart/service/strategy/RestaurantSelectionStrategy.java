package FoodKart.service.strategy;

import FoodKart.model.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantSelectionStrategy {

    Optional<Restaurant> selectRestaurant(
            List<Restaurant> restaurants,
            List<String> items);
}
