package FoodKart.service.strategy;

import FoodKart.model.Restaurant;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@Primary
public class HighestCapacityStrategy implements RestaurantSelectionStrategy {

    @Override
    public Optional<Restaurant> selectRestaurant(List<Restaurant> restaurants, List<String> items) {

        return restaurants.stream().max(Comparator.comparingInt(r ->
                r.getProcessingCapacity() - r.getActiveOrders().size()));
    }
}
