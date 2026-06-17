package testing;

import com.testloadbalancer.LeastConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class LeaseConnectionTest {

    @Test
    void picksServerWithFewestActiveConnections() {
        LeastConnection leastConnection = new LeastConnection();
        leastConnection.increaseConnection("server1");
        leastConnection.increaseConnection("server2");
        leastConnection.increaseConnection("server3");
        leastConnection.increaseConnection("server1");
        leastConnection.increaseConnection("server2");

        Assertions.assertEquals("server3",leastConnection.select(List.of("server1","server2","server3")));
    }

    @Test
    void releaseConnection_decrementCount() {
        LeastConnection leastConnection = new LeastConnection();
        leastConnection.increaseConnection("server1");
        leastConnection.increaseConnection("server2");
        leastConnection.increaseConnection("server3");
        leastConnection.increaseConnection("server1");
        leastConnection.increaseConnection("server2");
        leastConnection.increaseConnection("server3");
        leastConnection.decreaseConnection("server1");

        Assertions.assertEquals(1,leastConnection.getConnectionCountForServer("server1"));
        Assertions.assertEquals(2,leastConnection.getConnectionCountForServer("server2"));
        Assertions.assertEquals(2,leastConnection.getConnectionCountForServer("server3"));
    }

    @Test
    void neverGoesNegative() {
        LeastConnection leastConnection = new LeastConnection();
        leastConnection.increaseConnection("server1");
        leastConnection.increaseConnection("server2");
        leastConnection.increaseConnection("server3");
        leastConnection.increaseConnection("server1");
        leastConnection.increaseConnection("server2");
        leastConnection.increaseConnection("server3");
        leastConnection.decreaseConnection("server1");
        leastConnection.decreaseConnection("server1");
        leastConnection.decreaseConnection("server1");
        leastConnection.decreaseConnection("server1");

        Assertions.assertEquals(0,leastConnection.getConnectionCountForServer("server1"));
        Assertions.assertEquals(2,leastConnection.getConnectionCountForServer("server2"));
        Assertions.assertEquals(2,leastConnection.getConnectionCountForServer("server3"));
    }

    @Test
    void tieBreak_picksFirstInList() {
        LeastConnection leastConnection = new LeastConnection();
        leastConnection.increaseConnection("server1");
        leastConnection.increaseConnection("server2");
        leastConnection.increaseConnection("server3");
        leastConnection.increaseConnection("server1");
        leastConnection.increaseConnection("server2");
        leastConnection.increaseConnection("server3");
        Assertions.assertEquals("server1",leastConnection.select(List.of("server1","server2","server3")));
    }

    @Test void neverGoesNegative2() {
        var strategy = new LeastConnection();
        strategy.decreaseConnection("s1"); // decrement without prior increment
        Assertions.assertEquals(0, strategy.getConnectionCountForServer("s1"));
    }

    @Test void tieBreak_picksFirstInList2() {
        var strategy = new LeastConnection();
        // all at 0 — should pick first in list
        Assertions.assertEquals("s1",
                strategy.select(List.of("s1", "s2")));
    }
}
