package RoundRobinLoadBalancer;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer {

    private final List<String> servers;

    private final AtomicInteger index;

    public RoundRobinLoadBalancer() {

        servers = new ArrayList<>();

        index = new AtomicInteger(0);
    }

    public void addServer(String server) {

        servers.add(server);
    }

    public String getServer() {

        if (servers.isEmpty()) {
            return null;
        }

        int current =
                index.getAndIncrement();

        return servers.get(
                current % servers.size()
        );
    }

    public static void main(String[] args) {

        RoundRobinLoadBalancer lb =
                new RoundRobinLoadBalancer();

        lb.addServer("A");
        lb.addServer("B");
        lb.addServer("C");

        for (int i = 0; i < 10; i++) {
            System.out.println(lb.getServer());
        }
    }
}
