package Concurrent;

import java.util.concurrent.*;
import java.util.*;

public class ConcurrentMapLoadBalancer {

    private static final int MAX_SERVERS = 10;

    private final ConcurrentHashMap<String, Integer> map;
    private final CopyOnWriteArrayList<String> servers;
    private final Random random;

    public ConcurrentMapLoadBalancer() {
        map = new ConcurrentHashMap<>();
        servers = new CopyOnWriteArrayList<>();
        random = new Random();
    }

    public boolean addServer(String server) {

        if (servers.size() >= MAX_SERVERS) {
            return false;
        }

        if (map.putIfAbsent(server, servers.size()) != null) {
            return false;
        }

        servers.add(server);

        return true;
    }

    public boolean removeServer(String server) {

        Integer index = map.remove(server);

        if (index == null) {
            return false;
        }

        servers.remove(server);

        return true;
    }

    public String getServer() {

        if (servers.isEmpty()) {
            return null;
        }

        int index = random.nextInt(servers.size());

        return servers.get(index);
    }

    public static void main(String[] args) {

        ConcurrentMapLoadBalancer lb =
                new ConcurrentMapLoadBalancer();

        Runnable addTask = () -> {
            for (int i = 0; i < 5; i++) {
                lb.addServer(Thread.currentThread().getName() + "-S" + i);
            }
        };

        Thread t1 = new Thread(addTask);
        Thread t2 = new Thread(addTask);

        t1.start();
        t2.start();
    }
}
