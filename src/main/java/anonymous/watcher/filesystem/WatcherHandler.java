package anonymous.watcher.filesystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class WatcherHandler implements WebSocketHandler {

    @Autowired
    private WatcherSessionRepository watcherSessionRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        watcherSessionRepository.add(session.getId(), session);
        WatcherSession watcherSession = watcherSessionRepository.get(session.getId());
        watcherSession.sendMessage(new TextMessage("{\"proc\": \"connected\"}"));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        final WatcherSession watcherSession = watcherSessionRepository.get(session.getId());
        final JacksonJsonParser parser = new JacksonJsonParser();
        final Map<String, Object> payload = parser.parseMap(message.getPayload().toString());

        watcherSession.setWatchingDirectory(payload.get("directory").toString());

        if (!watcherSession.isSetBehavior()) {
            watcherSession.setThreadBehavior(() -> {
                FileSystem fs = null;
                WatchService fsWatcher = null;

                try {
                    Path watchingDirectory = Paths.get(watcherSession.getWatchingDirectory());
                    fs = watchingDirectory.getFileSystem();
                    fsWatcher = fs.newWatchService();

                    watchingDirectory.register(fsWatcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

                    boolean haveToSend = false;

                    while (!Thread.currentThread().isInterrupted()) {
                        WatchKey watchKey = fsWatcher.poll(500, TimeUnit.MILLISECONDS);
                        if (watchKey == null && haveToSend) {
                            watcherSession.sendMessage(new TextMessage("{\"proc\": \"changed\"}"));
                            haveToSend = false;
                        } else if (watchKey != null) {
                            List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                            if (watchEvents.size() > 0) haveToSend = true;
                            watchKey.reset();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fsWatcher != null) {
                        try {
                            fsWatcher.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            watcherSession.startThread();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        watcherSessionRepository.remove(session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
