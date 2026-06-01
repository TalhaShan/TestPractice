package com.testloadbalancer.normal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LoadBalancer {

    private static final int MAX_SERVERS = 10;

    private List<String> servers;
    private Map<String, Integer> serverIndexMap;
    private Random random;

    public LoadBalancer() {
        servers = new ArrayList<>();
        random = new Random();
        serverIndexMap = new HashMap<>();
    }

    public boolean addServer(String server){
      if(servers.size()>MAX_SERVERS){
          return false;
      }
      if(servers.contains(server)){
          return false;
        }
      servers.add(server);
      serverIndexMap.put(server, servers.size()-1);
      return true;
    }

    public boolean removeServer(String server) {
        if(!servers.contains(server) || !serverIndexMap.containsKey(server)){
            return false;
        }
        int index = serverIndexMap.get(server);
        String lastServer = servers.get(servers.size() - 1);

        // Swap with last element
        servers.set(index, lastServer);
        serverIndexMap.put(lastServer, index);

        // Remove last
        servers.remove(servers.size() - 1);
        serverIndexMap.remove(server);

        return true;



    }
    public String getServer() {
        if (servers.isEmpty()) {
            return null;
        }
        int index = random.nextInt(servers.size());
        return servers.get(index);

    }




    }
