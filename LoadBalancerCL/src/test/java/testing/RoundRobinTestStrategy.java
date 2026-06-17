package testing;

import com.testloadbalancer.LoadBalancer;
import com.testloadbalancer.RandomSelect;
import com.testloadbalancer.RoundRobinSelect;
import com.testloadbalancer.interfaces.RoutingStrategy;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class RoundRobinTestStrategy {

    @Test
    void roundRobin_SelectServesInOrder() {

        RoutingStrategy strategy = new RoundRobinSelect();
        List<String> servers = Arrays.asList("server1", "server2", "server3");

        Assertions.assertEquals("server1", strategy.select(servers));
        Assertions.assertEquals("server2", strategy.select(servers));
        Assertions.assertEquals("server3", strategy.select(servers));
        Assertions.assertEquals("server1", strategy.select(servers));
    }


    @Test
    void roundRobin_throwsOnEmptyList() {
        Assertions.assertThrows(IllegalStateException.class,
                () -> new RoundRobinSelect().select(List.of()));
    }

    @Test
    void routingRerunsSeveralTimes() {
        LoadBalancer lb = new LoadBalancer(new RoundRobinSelect());
        lb.register("server1");
        lb.register("server2");
        lb.register("server3");
        Assertions.assertEquals("server1", lb.route());
    }

    @Test
    void roundRobin_DistributesEvenly() {
       LoadBalancer lb = new LoadBalancer(new RoundRobinSelect());
       lb.register("server1");
       lb.register("server2");
       lb.register("server3");

       Assertions.assertEquals("server1", lb.route());
       Assertions.assertEquals("server2", lb.route());
       Assertions.assertEquals("server3", lb.route());
       Assertions.assertEquals("server1", lb.route());
       Assertions.assertEquals("server2", lb.route());
       Assertions.assertEquals("server3", lb.route());

    }

    @Test
    void random_AlwaysPickFromRegisteredServers() {
        LoadBalancer lb = new LoadBalancer(new RandomSelect());
        lb.register("server1");
        lb.register("server2");
        lb.register("server3");

        var Valid  = Set.of("server1", "server2", "server3");
        for(int i=0; i<100; i++){
            Assertions.assertTrue(Valid.contains(lb.route()));
        }
        }

}
