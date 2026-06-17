package com.testloadbalancer;

import com.testloadbalancer.interfaces.ILoadBalancer;
import com.testloadbalancer.interfaces.RoutingStrategy;

import java.util.concurrent.CopyOnWriteArrayList;

public class LoadBalancer implements ILoadBalancer {

  private final RoutingStrategy strategy;
  private final CopyOnWriteArrayList<String> servers = new CopyOnWriteArrayList<>();

    public LoadBalancer(RoutingStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public String register(String address) {
        if(address == null || address.isEmpty()){throw new IllegalArgumentException("Address cannot be null or empty");}
        if(servers.contains(address)){return address;}
        servers.add(address);
        return address;
    }

    @Override
    public void deregister(String address) {
      servers.remove(address);
    }

    @Override
    public String route() {
        if(servers.isEmpty()){ throw new IllegalStateException("No servers available");}
        return strategy.select(servers);
    }

    @Override
    public int size() {
        return servers.size();
    }
}
