package backend.WebSocketController;

import backend.WebSocketDTOs.WebSocketDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class WebSocketController {

    private final Set<String> sessionIds = ConcurrentHashMap.newKeySet();

    public Set<String> getSessionIdsReference() {
        return sessionIds;
    }

    @MessageMapping("/join-config-setup")
    @SendTo("/topic/player-count")
    public int handleJoin(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        sessionIds.add(sessionId);
        return Math.min(sessionIds.size(), 2);
    }

    @MessageMapping("/config-update")
    @SendTo("/topic/config-update")
    public WebSocketDTO handleConfigUpdate(@Payload WebSocketDTO config) {
        return config;
    }

    @MessageMapping("/config-confirmed")
    @SendTo("/topic/config-confirmed")
    public WebSocketDTO handleConfigConfirmed(@Payload WebSocketDTO config) {
        return config;
    }
}
