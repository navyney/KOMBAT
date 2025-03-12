package backend.WebSocketController;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final AtomicInteger playerCount;

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate, WebSocketController controller) {
        this.messagingTemplate = messagingTemplate;
        this.playerCount = controller.getPlayerCountReference(); // 👈 ใช้ตัวเดียวกันกับใน controller
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        int newCount = playerCount.decrementAndGet();
        System.out.println("🔌 A user disconnected. Remaining: " + newCount);
        messagingTemplate.convertAndSend("/topic/player-count", Math.max(newCount, 0));
    }
}
