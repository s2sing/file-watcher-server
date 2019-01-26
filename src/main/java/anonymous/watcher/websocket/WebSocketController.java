package anonymous.watcher.websocket;

import anonymous.watcher.filesystem.WatcherHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketController implements WebSocketConfigurer {

    @Autowired
    private WatcherHandler watcherHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(watcherHandler, "/watch").setAllowedOrigins("*");
    }

}
