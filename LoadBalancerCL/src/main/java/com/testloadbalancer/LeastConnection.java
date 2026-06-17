package com.testloadbalancer;

import com.testloadbalancer.interfaces.RoutingStrategy;

import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LeastConnection implements RoutingStrategy {

    private final ConcurrentHashMap <String, AtomicInteger> connections = new ConcurrentHashMap<>();


    public  void increaseConnection(String address) {
        connections.computeIfAbsent(address,k-> new AtomicInteger(0)).incrementAndGet();
    }

    public  void decreaseConnection(String address) {
       connections.computeIfAbsent(address,k-> new AtomicInteger(0)).
        updateAndGet(v-> Math.max(0,v-1)); //Dont go beyond zero
    }

    public int getConnectionCountForServer(String address) {
        return connections.getOrDefault(address,new AtomicInteger(0)).get();
    }


    @Override
    public String select(List<String> servers) {
        return servers.stream().min(Comparator.comparingInt(this::getConnectionCountForServer)).
                orElseThrow(()->new IllegalStateException("No servers found"));
    }
}
