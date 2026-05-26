package concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer implements LoadBalancer {

    private final CopyOnWriteArrayList<Server> servers = new CopyOnWriteArrayList<>();

    private final ConcurrentHashMap<String, Server> serverMap = new ConcurrentHashMap<>();

    private final AtomicInteger counter = new AtomicInteger(0);

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    public void register(Server server) {
        if (server == null) {
            throw new IllegalArgumentException("Server cannot be null");
        }

        Server existing = serverMap.putIfAbsent(server.getId(), server);

        if (existing == null) {
            servers.add(server);
        }
    }

    @Override
    public void remove(String serverId) {
        Server removed = serverMap.remove(serverId);

        if (removed != null) {
            servers.remove(removed);
        }
    }

    @Override
    public Server getNextServer() {

        List<Server> healthyServers = getHealthyServers();

        if (healthyServers.isEmpty()) {
            throw new NoSuchElementException("No healthy servers available");
        }

        int index = Math.abs(counter.getAndIncrement()) % healthyServers.size();

        return healthyServers.get(index);
    }

    @Override
    public CompletableFuture<Server> getNextServerAsync() {
        return CompletableFuture.supplyAsync(this::getNextServer, executorService);
    }

    @Override
    public List<Server> getAllServers() {
        return new ArrayList<>(servers);
    }

    @Override
    public int size() {
        return servers.size();
    }

    private List<Server> getHealthyServers() {
        List<Server> healthy = new ArrayList<>();

        for (Server server : servers) {
            if (server.isHealthy()) {
                healthy.add(server);
            }
        }

        return healthy;
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
