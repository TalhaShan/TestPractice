package com.testloadbalancer.interfaces;

public interface ILoadBalancer {
    String register(String server);
    void deregister(String server);
    String route();
    int size();
}
