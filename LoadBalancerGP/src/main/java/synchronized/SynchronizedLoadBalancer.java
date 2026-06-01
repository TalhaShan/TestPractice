import java.util.*;

public class SynchronizedLoadBalancer {

    private static final int MAX_SERVERS = 10;

    private final List<String> servers;
    private final Map<String, Integer> map;
    private final Random random;

    public SynchronizedLoadBalancer() {
        servers = new ArrayList<>();
        map = new HashMap<>();
        random = new Random();
    }

    public synchronized boolean addServer(String server) {

        if (servers.size() >= MAX_SERVERS) {
            return false;
        }

        if (map.containsKey(server)) {
            return false;
        }

        servers.add(server);
        map.put(server, servers.size() - 1);

        return true;
    }

    public synchronized boolean removeServer(String server) {

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
    }

    public synchronized String getServer() {

        if (servers.isEmpty()) {
            return null;
        }

        return servers.get(random.nextInt(servers.size()));
    }

    public static void main(String[] args) {

        SynchronizedLoadBalancer lb =
                new SynchronizedLoadBalancer();

        lb.addServer("A");
        lb.addServer("B");

        System.out.println(lb.getServer());

        lb.removeServer("A");

        System.out.println(lb.getServer());
    }
}
