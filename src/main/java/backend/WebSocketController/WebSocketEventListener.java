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
        AtomicInteger playerCount = WebSocketController.getPlayerCount();
        Map<String, String> sessionPlayerMap = WebSocketController.getSessionPlayerMap();

        if (sessionId != null && sessionIds.contains(sessionId)) {
            sessionIds.remove(sessionId);
            int remaining = Math.max(0, playerCount.decrementAndGet());
            System.out.println("⛔️ Player disconnected: " + sessionPlayerMap.get(sessionId));
            System.out.println("🔌 Remaining players: " + remaining);
            messagingTemplate.convertAndSend("/topic/player-count", remaining);

            // ✅ ใช้ sessionId ไปหา playerId
            String playerId = sessionPlayerMap.get(sessionId);
            sessionPlayerMap.remove(sessionId); // ล้าง mapping

            if (playerId != null) {
                if (playerId.equals(WebSocketController.getPlayer1Id())) {
                    WebSocketController.clearPlayer1();
                } else if (playerId.equals(WebSocketController.getPlayer2Id())) {
                    WebSocketController.clearPlayer2();
                }

                // ส่งข้อความไปยัง /topic/lock-all เพื่อแจ้งให้ผู้เล่นอื่นทราบว่าห้องไม่เต็มแล้ว
                messagingTemplate.convertAndSend("/topic/lock-all", Map.of("locked", false));
            }
        }
    }

//    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        String sessionId = headerAccessor.getSessionId();
//        String playerId = WebSocketController.getSessionPlayerMap().get(sessionId);
//        Set<String> sessionIds = WebSocketController.getSessionIds();
//        AtomicInteger playerCount = WebSocketController.getPlayerCount();
//
//        if (sessionId != null && sessionIds.contains(sessionId)) {
//            sessionIds.remove(sessionId);
//            int remaining = Math.max(0, playerCount.decrementAndGet());
//            System.out.println("🔌 A user disconnected. Remaining: " + remaining);
//            messagingTemplate.convertAndSend("/topic/player-count", remaining);
//
//            if (sessionId.equals(WebSocketController.getPlayer1Id())) {
//                WebSocketController.clearPlayer1();
//            } else if (sessionId.equals(WebSocketController.getPlayer2Id())) {
//                WebSocketController.clearPlayer2();
//            }
//        }
//    }

}
