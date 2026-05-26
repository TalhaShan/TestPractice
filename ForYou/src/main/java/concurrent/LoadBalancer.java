package concurrent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface LoadBalancer {

    void register(Server server);

    void remove(String serverId);

    Server getNextServer();

    CompletableFuture<Server> getNextServerAsync();

    List<Server> getAllServers();

    int size();
}
