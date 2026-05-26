package concurrent;

import java.util.Objects;

public class Server {

    private final String id;
    private volatile boolean healthy;

    public Server(String id) {
        this.id = id;
        this.healthy = true;
    }

    public String getId() {
        return id;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Server)) return false;
        Server server = (Server) o;
        return Objects.equals(id, server.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Server{" +
                "id='" + id + '\'' +
                ", healthy=" + healthy +
                '}';
    }
}
