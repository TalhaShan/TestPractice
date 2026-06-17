package com.testloadbalancer;

import com.testloadbalancer.interfaces.ILoadBalancer;
import com.testloadbalancer.interfaces.RoutingStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLoadBalancer implements ILoadBalancer {

    private final List<String> servers=new ArrayList<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock=lock.readLock();
    private final Lock writeLock=lock.writeLock();
    private final RoutingStrategy routingStrategy;

    public ReadWriteLoadBalancer(RoutingStrategy routingStrategy) {
        this.routingStrategy = routingStrategy;
    }


    @Override
    public String register(String server) {
        writeLock.lock(); //exclusive block all reader
        try{
        if(!servers.contains(server)){
            servers.add(server);
        }
        }finally {
            writeLock.unlock();
        }
        return server;
    }

    @Override
    public void deregister(String server) {
        writeLock.lock();
        try{
            if(servers.contains(server)){
                servers.remove(server);
            }
        }finally {
            writeLock.unlock();
        }
    }


    @Override
    public String route() {
        readLock.lock();                    // shared — many readers in parallel
        try {
            if (servers.isEmpty()) throw new IllegalStateException("No servers");
            return routingStrategy.select(Collections.unmodifiableList(servers));
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int size() {
        readLock.lock();
        try{
            return servers.size();
        }
        finally {
            readLock.unlock();
        }
    }
}
