package backend.WebSocketController;

import backend.WebSocketDTOs.WebSocketDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @Getter
    private static final Set<String> sessionIds = new HashSet<>();
    @Getter
    private static final AtomicInteger playerCount = new AtomicInteger(0);
    @Getter
    public static String player1Id = null;
    @Getter
    public static String player2Id = null;
    @Getter
    private static String selectedMode = null;
    @Getter
    private static final Map<String, String> sessionPlayerMap = new HashMap<>();

    @MessageMapping("/join-game")
    public void handleJoinGame(@Payload Map<String, String> payload, SimpMessageHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        String playerId = payload.get("playerId");

        if (!sessionIds.contains(sessionId)) {
            sessionIds.add(sessionId);
            sessionPlayerMap.put(sessionId, playerId);
            System.out.println("✅ Player connected: " + playerId);
        }

        // Send current state to the new player
        if (selectedMode != null) {
            messagingTemplate.convertAndSendToUser(sessionId, "/topic/lock-mode", Map.of("selectedMode", selectedMode));
        }

        if (player1Id != null && player2Id != null) {
            messagingTemplate.convertAndSendToUser(sessionId, "/topic/lock-all", Map.of("locked", true));
            messagingTemplate.convertAndSendToUser(sessionId, "/topic/role-assigned", Map.of(
                    "role", "spectator",
                    "playerId", playerId,
                    "disableButtons", true
            ));
        } else if (player1Id != null) {
            // หากมี player1 อยู่แล้ว แต่ยังไม่มี player2
            messagingTemplate.convertAndSendToUser(sessionId, "/topic/lock-mode", Map.of("selectedMode", selectedMode));
        }
    }

    @MessageMapping("/select-mode")
    public void handleSelectMode(@Payload Map<String, String> payload, SimpMessageHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        String playerId = payload.get("playerId");
        String mode = payload.get("mode");

        if (player1Id != null && player2Id != null) {
            messagingTemplate.convertAndSend("/topic/lock-all", Map.of("locked", true));
            messagingTemplate.convertAndSend("/topic/role-assigned", Map.of(
                    "role", "spectator",
                    "playerId", playerId,
                    "disableButtons", true
            ));
            System.out.println("❌ Room is full. Assigning spectator to: " + playerId);
            return;
        }

        if (player1Id == null) {
            player1Id = playerId;
            selectedMode = mode;
            messagingTemplate.convertAndSend("/topic/role-assigned", Map.of(
                    "role", "player1",
                    "playerId", playerId,
                    "disableButtons", false
            ));
            messagingTemplate.convertAndSend("/topic/lock-mode", Map.of("selectedMode", selectedMode));
            System.out.println("🎮 select-mode: " + playerId + " -> " + mode);
            System.out.println("✅ current player1: " + player1Id + ", player2: " + player2Id);
            return;
        }

        if ("pvp".equals(selectedMode) && player2Id == null && !playerId.equals(player1Id)) {
            player2Id = playerId;
            messagingTemplate.convertAndSend("/topic/role-assigned", Map.of(
                    "role", "player2",
                    "playerId", playerId,
                    "disableButtons", true
            ));
            messagingTemplate.convertAndSend("/topic/lock-all", Map.of("locked", true));
            System.out.println("🎮 select-mode: " + playerId + " -> " + mode);
            System.out.println("✅ current player1: " + player1Id + ", player2: " + player2Id);
            return;
        }

        messagingTemplate.convertAndSend("/topic/role-assigned", Map.of(
                "role", "spectator",
                "playerId", playerId,
                "disableButtons", true
        ));
        System.out.println("🎮 select-mode: " + playerId + " -> " + mode);
        System.out.println("✅ current player1: " + player1Id + ", player2: " + player2Id);
    }

    @MessageMapping("/request-lock-status")
    public void handleRequestLockStatus(@Payload Map<String, String> payload, SimpMessageHeaderAccessor accessor) {
        String playerId = payload.get("playerId");

        if (selectedMode != null) {
            messagingTemplate.convertAndSend("/topic/lock-mode", Map.of("selectedMode", selectedMode));
        }

        if (player1Id != null && player2Id != null) {
            messagingTemplate.convertAndSend("/topic/lock-all", Map.of("locked", true));
        }
    }

    @MessageMapping("/join-config-setup")
    public void handleJoinConfig(@Payload Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String playerId = payload.get("playerId");

        if (!sessionIds.contains(sessionId)) {
            sessionIds.add(sessionId);
            sessionPlayerMap.put(sessionId, playerId);
            int count = playerCount.incrementAndGet();
            messagingTemplate.convertAndSend("/topic/player-count", Math.min(count, 2));
        }
    }

    @MessageMapping("/config-update")
    public void handleConfigUpdate(@Payload WebSocketDTO config) {
        System.out.println("📥 CONFIG RECEIVED FROM FRONTEND: " + config);
        messagingTemplate.convertAndSend("/topic/config-update", config);
        messagingTemplate.convertAndSend("/topic/config-reset-confirmed", config.getPlayerId());
    }

    @MessageMapping("/config-confirmed")
    public void handleConfigConfirmed(@Payload WebSocketDTO dto) {
        System.out.println("✅ CONFIRM RECEIVED FROM: " + dto.getPlayerId());
        messagingTemplate.convertAndSend("/topic/config-confirmed", dto);
    }

    @MessageMapping("/navigate")
    public void handleNavigation(@Payload String action) {
        messagingTemplate.convertAndSend("/topic/navigate", action);
    }

    public static void clearPlayer1() {
        player1Id = null;
        selectedMode = null;
    }

    public static void clearPlayer2() {
        player2Id = null;
    }

}
