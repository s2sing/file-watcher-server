package anonymous.watcher.filesystem;

import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class WatcherSession {

    private WebSocketSession session;
    private Thread thread;

    private String watchingDirectory;

    public WatcherSession(WebSocketSession session) {
        this.session = session;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void setThreadBehavior(Runnable runnable) {
        this.thread = new Thread(runnable);
    }

    public boolean isSetBehavior() {
        return this.thread != null;
    }

    public void startThread() {
        this.thread.start();
    }

    public void stopThread() {
        this.thread.interrupt();
    }

    public void setWatchingDirectory(String watchingDirectory) {
        this.watchingDirectory = watchingDirectory;
    }

    public String getWatchingDirectory() {
        return watchingDirectory;
    }

    public void sendMessage(WebSocketMessage<?> message) {
        try {
            this.session.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
