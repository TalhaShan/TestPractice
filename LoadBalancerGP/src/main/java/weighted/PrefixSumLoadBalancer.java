package weighted;

import java.util.*;

public class PrefixSumLoadBalancer {

    private final TreeMap<Integer, String> map;

    private int totalWeight;

    private final Random random;

    public PrefixSumLoadBalancer() {

        map = new TreeMap<>();

        random = new Random();

        totalWeight = 0;
    }

    public void addServer(String server, int weight) {

        totalWeight += weight;

        map.put(totalWeight, server);
    }

    public String getServer() {

        int value = random.nextInt(totalWeight) + 1;

        return map.ceilingEntry(value).getValue();
    }

    public static void main(String[] args) {

        PrefixSumLoadBalancer lb =
                new PrefixSumLoadBalancer();

        lb.addServer("A", 3);

        lb.addServer("B", 1);

        for (int i = 0; i < 20; i++) {
            System.out.println(lb.getServer());
        }
    }
}
