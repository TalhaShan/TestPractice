package FoodKart.service;

import FoodKart.model.Restaurant;

import java.util.List;

public interface RestaurantService {

    void onboardRestaurant(Restaurant restaurant);

    void updatePrice(String restaurantId, String itemName, double price);

    List<Restaurant> getAllRestaurants();
}
