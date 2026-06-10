package FoodKart.service.strategy;

import FoodKart.model.Restaurant;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class LowestPriceStrategy implements RestaurantSelectionStrategy {

    @Override
    public Optional<Restaurant> selectRestaurant(List<Restaurant> restaurants, List<String> items) {

        return restaurants.stream().min(Comparator.comparingDouble(r -> items.stream().mapToDouble(i ->
                r.getMenu().get(i).getPrice()).sum()));
    }
}
