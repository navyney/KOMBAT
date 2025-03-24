package backend.WebSocketController;

import backend.WebSocketDTOs.MinionConfigMessage;
import backend.WebSocketDTOs.WebSocketDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import backend.WebSocketDTOs.MinionType;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private static WebSocketDTO currentConfig;

    @Getter
    //private static final Set<String> sessionIds = new HashSet<>();
    private static final Set<String> sessionIds = ConcurrentHashMap.newKeySet();
    @Getter
    private static final AtomicInteger playerCount = new AtomicInteger(0);
    @Getter
    public static String player1Id = null;
    @Getter
    public static String player2Id = null;
    @Getter
    private static String selectedMode = null;
    @Getter
    //private static final Map<String, String> sessionPlayerMap = new HashMap<>();
    private static final Map<String, String> sessionPlayerMap = new ConcurrentHashMap<>();

    @MessageMapping("/join-game")
    public void handleJoinGame(@Payload Map<String, String> payload, SimpMessageHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        String playerId = payload.get("playerId");

        sessionIds.add(sessionId); // ใส่ซ้ำได้ เพราะใช้ Set
        sessionPlayerMap.put(sessionId, playerId); // ✅ always map sessionId → playerId
        System.out.println("✅ Player connected: " + playerId);

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

        boolean assigned = false;

        if (player1Id != null && player2Id != null) {
            messagingTemplate.convertAndSend("/topic/lock-all", Map.of("locked", true));
            messagingTemplate.convertAndSend("/topic/role-assigned", Map.of(
                    "role", "spectator",
                    "playerId", playerId,
                    "disableButtons", true
            ));
            System.out.println("❌ Room is full. Assigning spectator to: " + playerId);
        } else if (player1Id == null) {
            player1Id = playerId;
            selectedMode = mode;
            messagingTemplate.convertAndSend("/topic/role-assigned", Map.of(
                    "role", "player1",
                    "playerId", playerId,
                    "disableButtons", false
            ));
            messagingTemplate.convertAndSend("/topic/lock-mode", Map.of("selectedMode", selectedMode));
            System.out.println("🎮 select-mode: " + playerId + " -> " + mode);
            assigned = true;
        } else if ("pvp".equals(selectedMode) && player2Id == null && !playerId.equals(player1Id)) {
            player2Id = playerId;
            messagingTemplate.convertAndSend("/topic/role-assigned", Map.of(
                    "role", "player2",
                    "playerId", playerId,
                    "disableButtons", true
            ));
            messagingTemplate.convertAndSend("/topic/lock-all", Map.of("locked", true));
            System.out.println("🎮 select-mode: " + playerId + " -> " + mode);
            assigned = true;
        } else {
            messagingTemplate.convertAndSend("/topic/role-assigned", Map.of(
                    "role", "spectator",
                    "playerId", playerId,
                    "disableButtons", true
            ));
            System.out.println("👀 Assigned spectator to: " + playerId);
        }

        // ✅ Always update player count after role assignment
        int count = 0;
        if (player1Id != null) count++;
        if (player2Id != null) count++;

        messagingTemplate.convertAndSend("/topic/player-count", count);
        System.out.println("👥 Updated player count: " + count);
        System.out.println("🧍 player1: " + player1Id + ", 🧍 player2: " + player2Id);
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

//    @MessageMapping("/join-config-setup")
//    public void handleJoinConfig(@Payload Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
//        String sessionId = headerAccessor.getSessionId();
//        String playerId = payload.get("playerId");
////        if (!sessionIds.contains(sessionId)) {
////            sessionIds.add(sessionId);
////            sessionPlayerMap.put(sessionId, playerId);
////            int count = playerCount.incrementAndGet();
////            messagingTemplate.convertAndSend("/topic/player-count", Math.min(count, 2));
////        }
//
//        if (!sessionPlayerMap.containsKey(sessionId)) {
//            sessionIds.add(sessionId);
//            sessionPlayerMap.put(sessionId, playerId);
//        }
//
//        long count = sessionPlayerMap.values().stream().distinct().count();
//        messagingTemplate.convertAndSend("/topic/player-count", Math.min((int) count, 2));
//    }

//    @MessageMapping("/join-config-setup")
//    public void handleJoinConfig(@Payload Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
//        String sessionId = headerAccessor.getSessionId();
//        String playerId = payload.get("playerId");
//
//        // ตรวจสอบว่ามี sessionId นี้หรือยัง
//        if (!sessionPlayerMap.containsKey(sessionId)) {
//            sessionIds.add(sessionId); // สำหรับ tracking session
//            sessionPlayerMap.put(sessionId, playerId); // ผูก sessionId กับ playerId
//        }
//
//        // ✅ นับจำนวน playerId ที่ไม่ซ้ำกัน (distinct) จากทุก session
//        long distinctPlayerCount = sessionPlayerMap.values().stream().distinct().count();
//
//        // ✅ จำกัดไว้ไม่ให้เกิน 2 คน
//        int limitedCount = Math.min((int) distinctPlayerCount, 2);
//
//        // ส่งจำนวนผู้เล่นไปยัง frontend
//        messagingTemplate.convertAndSend("/topic/player-count", limitedCount);
//
//        System.out.println("👥 Players in Config-set-up: " + distinctPlayerCount);
//    }

    @MessageMapping("/join-config-setup")
    public void handleJoinConfig(@Payload Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String playerId = payload.get("playerId");

        // เก็บ sessionId กับ playerId เพื่อใช้งานอื่น (optional)
        if (!sessionPlayerMap.containsKey(sessionId)) {
            sessionIds.add(sessionId);
            sessionPlayerMap.put(sessionId, playerId);
        }

        // ✅ นับจำนวน player จริง (ที่เลือก mode แล้ว)
        int count = 0;
        if (player1Id != null) count++;
        if (player2Id != null) count++;

        messagingTemplate.convertAndSend("/topic/player-count", count);
        System.out.println("👥 Players in Config-set-up (real players): " + count);
    }

    @MessageMapping("/config-update")
    public void handleConfigUpdate(@Payload WebSocketDTO config) {
        currentConfig = config;
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
        if ("back".equals(action)) {
            System.out.println("🔁 Resetting game state and navigating back to select-mode");
            WebSocketController.clearPlayer1();
            WebSocketController.clearPlayer2();
            WebSocketController.resetGameState();
        }
        messagingTemplate.convertAndSend("/topic/navigate", action);
    }

    public static void clearPlayer1() {
        player1Id = null;
        selectedMode = null;
    }

    public static void clearPlayer2() {
        player2Id = null;
    }

    @MessageMapping("/request-current-config")
    @SendTo("/topic/config-update")
    public WebSocketDTO getCurrentConfig() {
        return currentConfig;
    }

    public static void resetGameState() {
        player1Id = null;
        player2Id = null;
        selectedMode = null;
        sessionPlayerMap.clear();
        sessionIds.clear();
        System.out.println("🔁 Game state has been fully reset.");
    }

    @MessageMapping("/minion-config")
    public void handleMinionConfig(@Payload MinionConfigMessage message, Message<?> rawMessage) {
        String playerId = message.getPlayerId();
        List<MinionType> minions = message.getMinions();

        System.out.println("🧠 ได้รับ Minion config จาก playerId: " + playerId);

        if (minions == null || minions.isEmpty()) {
            System.out.println("⚠️ ไม่พบ Minions ที่ถูกส่งมา");
            return;
        }

        for (MinionType minion : minions) {
            System.out.println("➡️ Minion ID: " + minion.getId());
            System.out.println("   Name     : " + minion.getName());
            System.out.println("   DEF      : " + minion.getDef());
            System.out.println("   Strategy : " + minion.getStrategy());
            System.out.println("------------------------------");
        }

        messagingTemplate.convertAndSend("/topic/minion-updated", message);
    }

    @MessageMapping("/join-select-minion-type")
    public void handleJoinSelectMinionType(@Payload Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String playerId = payload.get("playerId");

        // เก็บ sessionId กับ playerId เพื่อใช้งานอื่น (optional)
        if (!sessionPlayerMap.containsKey(sessionId)) {
            sessionIds.add(sessionId);
            sessionPlayerMap.put(sessionId, playerId);
        }

        // ✅ นับจำนวน player จริง (ที่เลือก mode แล้ว)
        int count = 0;
        if (player1Id != null) count++;
        if (player2Id != null) count++;

        messagingTemplate.convertAndSend("/topic/player-count", count);
        System.out.println("👥 Players in Config-set-up (real players): " + count);
    }

    @MessageMapping("/minion-select")
    public void handleMinionToggle(@Payload Map<String, Object> payload) {
        String playerId = (String) payload.get("playerId");
        String buttonId = (String) payload.get("id");
        Boolean isSelected = (Boolean) payload.get("isSelected");

        System.out.println("🔘 [Toggle] Player: " + playerId + " toggled " + buttonId +
                " -> isSelected: " + isSelected);

        // ✅ ส่งให้ทุกคน
        messagingTemplate.convertAndSend("/topic/minion-select", payload);
    }

    @MessageMapping("/minion-customize")
    public void handleCustomize(@Payload Map<String, Object> payload) {
        String playerId = (String) payload.get("playerId");

        System.out.println("🔘 [Toggle] Player: " + playerId + " customized ");
        messagingTemplate.convertAndSend("/topic/minion-customize", payload);
    }

    @MessageMapping("/topic/minion-customize-apply")
    public void handleCustomizeApply(@Payload Map<String, Object> payload) {
        String playerId = (String) payload.get("playerId");

        System.out.println("🔘 Player: " + playerId + "has modal");
        messagingTemplate.convertAndSend("/topic/minion-customize-apply", payload);
    }

    @MessageMapping("/minion-close-modal")
    public void handleCloseModal(@Payload Map<String, Object> payload) {
        String playerId = (String) payload.get("playerId");

        System.out.println("🔘 Player: " + playerId + "has close modal");
        messagingTemplate.convertAndSend("/topic/minion-close-modal", payload);
    }

}
