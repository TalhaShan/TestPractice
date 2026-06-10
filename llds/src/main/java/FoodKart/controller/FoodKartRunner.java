package FoodKart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("foodkart")
@RequiredArgsConstructor
public class FoodKartRunner
        implements CommandLineRunner {

    private final CommandProcessor processor;

    @Override
    public void run(String... args) {

        List<String> commands =
                List.of(
                        "5,place-order,order2,item1,item2,item3",
                        "2,update-price,r1,item1,50",
                        "8,dispatch-order,order2",
                        "3,place-order,order1,item1"
                );

        processor.process(commands);
    }
}
