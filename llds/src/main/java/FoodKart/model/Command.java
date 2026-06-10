package FoodKart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Command {

    private long timestamp;

    private String operation;

    private List<String> arguments;
}
