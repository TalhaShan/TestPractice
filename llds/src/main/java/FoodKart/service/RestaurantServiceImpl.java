package FoodKart.service;

import FoodKart.model.MenuItem;
import FoodKart.model.Restaurant;
import FoodKart.repo.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository repository;

    @Override
    public void onboardRestaurant(Restaurant restaurant) {

        repository.save(restaurant);
    }

    @Override
    public void updatePrice(String restaurantId, String itemName, double price) {

        Restaurant restaurant = repository.findById(restaurantId);

        if (restaurant == null) {
            throw new RuntimeException("Restaurant not found");
        }

        restaurant.getMenu().put(itemName, new MenuItem(itemName, price));
    }

    @Override
    public List<Restaurant> getAllRestaurants() {

        return new ArrayList<>(repository.findAll());
    }
}
