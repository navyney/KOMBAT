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

            // ✅ ถ้า player ที่หลุดคือ player1 หรือ player2 → รีเซ็ตเกมทั้งหมด
            if (playerId != null &&
                    (playerId.equals(WebSocketController.getPlayer1Id()) || playerId.equals(WebSocketController.getPlayer2Id()))) {

                WebSocketController.resetGameState();

                // ✅ เตะทุกคนกลับหน้า start
                messagingTemplate.convertAndSend("/topic/navigate", "start");

                // ✅ ส่ง player-count = 0
                messagingTemplate.convertAndSend("/topic/player-count", 0);

                // ✅ ปลด lock
                messagingTemplate.convertAndSend("/topic/lock-all", Map.of("locked", false));

                System.out.println("🚨 A player disconnected. Full reset triggered.");
            }
        }
    }

//    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        String sessionId = headerAccessor.getSessionId();
//
//        Set<String> sessionIds = WebSocketController.getSessionIds();
//        Map<String, String> sessionPlayerMap = WebSocketController.getSessionPlayerMap();
//
//        if (sessionId != null && sessionIds.contains(sessionId)) {
//            sessionIds.remove(sessionId);
//
//            String playerId = sessionPlayerMap.get(sessionId);
//            sessionPlayerMap.remove(sessionId);
//
//            System.out.println("⛔️ Player disconnected: " + playerId);
//
//            long count = sessionPlayerMap.values().stream().distinct().count();
//            messagingTemplate.convertAndSend("/topic/player-count", Math.min((int) count, 2));
//            System.out.println("👥 Updated player count: " + count);
//
//            if (playerId != null) {
//                if (playerId.equals(WebSocketController.getPlayer1Id())) {
//                    WebSocketController.clearPlayer1();
//                } else if (playerId.equals(WebSocketController.getPlayer2Id())) {
//                    WebSocketController.clearPlayer2();
//                }
//
//                messagingTemplate.convertAndSend("/topic/lock-all", Map.of("locked", false));
//            }
//        }
//    }

}
