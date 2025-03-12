package backend.WebSocketController;

import backend.WebSocketDTOs.WebSocketDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final AtomicInteger playerCount = new AtomicInteger(0);

    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public AtomicInteger getPlayerCountReference() {
        return playerCount; // 👈 ให้ EventListener ดึงมาใช้
    }

    @MessageMapping("/join-config-setup")
    @SendTo("/topic/player-count")
    public int handleJoin() {
        return Math.min(playerCount.incrementAndGet(), 2);
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
