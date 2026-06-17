package base;

import com.testloadbalancer.LoadBalancer;
import com.testloadbalancer.RandomSelect;
import com.testloadbalancer.RoundRobinSelect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LoadBalancerTest {

    @Test
    void register_ServerAdded() {
        LoadBalancer lb = new LoadBalancer(new RoundRobinSelect());
        lb.register("server1");
        Assertions.assertEquals(1, lb.size());
    }

    @Test
    void register_IsIdempotent() {
        LoadBalancer lb = new LoadBalancer(new RoundRobinSelect());
        lb.register("server1");
        lb.register("server1");
        Assertions.assertEquals(1, lb.size());
    }

    @Test
    void register_ThrowsOnNullOrEmpty() {
        LoadBalancer lb = new LoadBalancer(new RoundRobinSelect());
        Assertions.assertThrows(IllegalArgumentException.class, () -> lb.register(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> lb.register(""));
    }


}
