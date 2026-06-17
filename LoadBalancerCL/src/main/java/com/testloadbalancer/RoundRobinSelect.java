package com.testloadbalancer;

import com.testloadbalancer.interfaces.RoutingStrategy;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinSelect implements RoutingStrategy {

    private final AtomicInteger counter = new AtomicInteger(0);
    @Override
    public String select(List<String> servers) {

        if(servers.isEmpty()){throw new IllegalStateException("No servers available");}
        int index = counter.getAndIncrement() % servers.size();
        return servers.get(index);

    }
}
