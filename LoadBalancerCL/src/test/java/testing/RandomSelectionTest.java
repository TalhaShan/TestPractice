package testing;

import com.testloadbalancer.RandomSelect;
import com.testloadbalancer.interfaces.RoutingStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.Random;

public class RandomSelectionTest {

    @Test
    void random_SelectsRandomly() {
        RoutingStrategy strategy = new RandomSelect();
        List<String> serversPool = List.of("server1", "server2", "server3");

        for(int i = 0; i < 50; i++){
           Assertions.assertTrue(serversPool.contains(strategy.select(serversPool)));
        }
    }

    @Test
    void random_throwsOnEmptyList() {
        Assertions.assertThrows(IllegalStateException.class,
                () -> new RandomSelect().select(List.of()));
    }

    @Test
    void random_deterministicOnSameSeedData(){
        Random seeded = new Random(42L);
        RoutingStrategy strategy = new RandomSelect(seeded);
        List<String> serversPool = List.of("server1", "server2", "server3");
        String first = strategy.select(serversPool);
        Assertions.assertEquals(first, new RandomSelect(new Random(42L)).select(serversPool));
    }
}
