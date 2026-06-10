package FoodKart.controller;

import FoodKart.model.Command;
import FoodKart.model.Order;
import FoodKart.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandProcessor {

    private final OrderService orderService;

    public void process(List<String> commands) {

        List<Command> parsed = commands.stream().map(this::parse).sorted(Comparator.comparingLong(Command::getTimestamp)).toList();

        for (Command command : parsed) {

            switch (command.getOperation()) {

                case "place-order" -> placeOrder(command);

                case "dispatch-order" -> dispatchOrder(command);

                default -> System.out.println("Ignoring command");
            }
        }
    }

    private void placeOrder(Command command) {

        String orderId = command.getArguments().get(0);

        List<String> items = command.getArguments().subList(1, command.getArguments().size());

        Order order = orderService.placeOrder(orderId, items, command.getTimestamp());

        System.out.println(order);
    }

    private void dispatchOrder(Command command) {

        orderService.dispatchOrder(command.getArguments().get(0));
    }

    private Command parse(String line) {

        String[] tokens = line.split(",");

        return new Command(Long.parseLong(tokens[0].trim()), tokens[1].trim(), Arrays.stream(tokens).skip(2).map(String::trim).toList());
    }
}
