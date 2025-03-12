package backend.WebSocketController;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Set;

@Component
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final Set<String> sessionIds;

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate, WebSocketController controller) {
        this.messagingTemplate = messagingTemplate;
        this.sessionIds = controller.getSessionIdsReference();
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        if (sessionId != null && sessionIds.remove(sessionId)) {
            System.out.println("🔌 A user disconnected. Remaining: " + sessionIds.size());
            messagingTemplate.convertAndSend("/topic/player-count", sessionIds.size());
        }
    }
}
