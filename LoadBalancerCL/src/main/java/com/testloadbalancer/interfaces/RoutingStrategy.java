package com.testloadbalancer.interfaces;

import java.util.List;

public interface RoutingStrategy {

    String select(List<String> servers);
}
