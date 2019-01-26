package anonymous.watcher.filesystem;

import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@Repository
public class WatcherSessionRepository {

    private Map<String, WatcherSession> users;

    public WatcherSessionRepository() {
        this.users = new HashMap<>();
    }

    public void add(String id, WebSocketSession session) {
        WatcherSession watcherSession = new WatcherSession(session);
        this.users.put(id, watcherSession);
    }

    public void remove(String id) {
        this.users.get(id).stopThread();
        this.users.remove(id);
    }

    public WatcherSession get(String id) {
        return this.users.get(id);
    }

    public Map<String, WatcherSession> getAll() {
        return this.users;
    }

}
