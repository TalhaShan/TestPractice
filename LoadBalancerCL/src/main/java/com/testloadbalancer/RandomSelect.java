package com.testloadbalancer;

import com.testloadbalancer.interfaces.RoutingStrategy;

import java.util.List;
import java.util.Random;

public class RandomSelect implements RoutingStrategy {

    private final Random random;

    // inject Random for testability — seed it in tests
    public RandomSelect() { this(new Random()); }

    public RandomSelect(Random random) {
        this.random = random;
    }

    @Override
    public String select(List<String> servers) {
        if(servers.isEmpty()){throw new IllegalStateException("No servers available");}
        int index = random.nextInt(servers.size());
        return servers.get(index);
    }
}
