package FoodKart.dto;

import lombok.Data;

@Data
public class UpdatePriceRequest {

    private String itemName;

    private double price;
}
