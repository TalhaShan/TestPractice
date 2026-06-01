package readWrtieLock;

import java.util.*;
import java.util.concurrent.locks.*;

public class ReadWriteLockLoadBalancer {

    private static final int MAX_SERVERS = 10;

    private final List<String> servers;
    private final Map<String, Integer> map;

    private final ReadWriteLock lock;

    private final Random random;

    public ReadWriteLockLoadBalancer() {

        servers = new ArrayList<>();
        map = new HashMap<>();

        lock = new ReentrantReadWriteLock();

        random = new Random();
    }

    public boolean addServer(String server) {

        lock.writeLock().lock();

        try {

            if (servers.size() >= MAX_SERVERS) {
                return false;
            }

            if (map.containsKey(server)) {
                return false;
            }

            servers.add(server);

            map.put(server, servers.size() - 1);

            return true;

        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean removeServer(String server) {

        lock.writeLock().lock();

        try {

            if (!map.containsKey(server)) {
                return false;
            }

            int index = map.get(server);

            String last = servers.get(servers.size() - 1);

            servers.set(index, last);

            map.put(last, index);

            servers.remove(servers.size() - 1);

            map.remove(server);

            return true;

        } finally {
            lock.writeLock().unlock();
        }
    }

    public String getServer() {

        lock.readLock().lock();

        try {

            if (servers.isEmpty()) {
                return null;
            }

            return servers.get(random.nextInt(servers.size()));

        } finally {
            lock.readLock().unlock();
        }
    }
}
