package backend.WebSocketController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;


    @Autowired
    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        Set<String> sessionIds = WebSocketController.getSessionIds();
        Map<String, String> sessionPlayerMap = WebSocketController.getSessionPlayerMap();

        if (sessionId != null && sessionIds.contains(sessionId)) {
            sessionIds.remove(sessionId);

            String playerId = sessionPlayerMap.get(sessionId);
            sessionPlayerMap.remove(sessionId);

            System.out.println("⛔️ Player disconnected: " + playerId);

            if (playerId != null &&
                    (playerId.equals(WebSocketController.getPlayer1Id()) || playerId.equals(WebSocketController.getPlayer2Id()))) {

                WebSocketController.resetGameState();
                messagingTemplate.convertAndSend("/topic/navigate", "start");
                messagingTemplate.convertAndSend("/topic/player-count", 0);
                messagingTemplate.convertAndSend("/topic/lock-all", Map.of("locked", false));

                System.out.println("🚨 A player disconnected. Full reset triggered.");
            }
        }
    }

}
