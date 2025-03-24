package backend.WebSocketController;

import backend.KOMBOOD.entity.Player;
import backend.KOMBOOD.error.EvalError;
import backend.KOMBOOD.error.LexicalError;
import backend.KOMBOOD.game.GameMode;
import backend.KOMBOOD.game.GameModeType;
import backend.KOMBOOD.game.GameState;

import backend.KOMBOOD.game.SetUpGameStage;
import backend.WebSocketDTOs.ActionOnHexGrid;
import backend.WebSocketDTOs.WebSocketDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

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
    @Getter
    private static SetUpGameStage gameState;
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

    @MessageMapping("/join-config-setup")
    public void handleJoinConfig(@Payload Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String playerId = payload.get("playerId");

        // ตรวจสอบว่ามี sessionId นี้หรือยัง
        if (!sessionPlayerMap.containsKey(sessionId)) {
            sessionIds.add(sessionId); // สำหรับ tracking session
            sessionPlayerMap.put(sessionId, playerId); // ผูก sessionId กับ playerId
        }

        // ✅ นับจำนวน playerId ที่ไม่ซ้ำกัน (distinct) จากทุก session
        long distinctPlayerCount = sessionPlayerMap.values().stream().distinct().count();

        // ✅ จำกัดไว้ไม่ให้เกิน 2 คน
        int limitedCount = Math.min((int) distinctPlayerCount, 2);

        // ส่งจำนวนผู้เล่นไปยัง frontend
        messagingTemplate.convertAndSend("/topic/player-count", limitedCount);

        System.out.println("👥 Players in Config-set-up: " + distinctPlayerCount);
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

    @MessageMapping("/minion/setup")
    @SendTo("/topic/setup")
    public void setUpGameStage() throws LexicalError, EvalError, IOException {
        String nameP1 = "player1Id";
        String nameP2 = "player2Id";
        Player player1 = new Player(nameP1);
        Player player2 = new Player(nameP2);
        GameMode gameMode = new GameMode();
        gameMode.setGameMode(GameModeType.DUEL);
        gameState = new SetUpGameStage(player1,player2,gameMode);
        gameState.setUP();
        System.out.println("setup complete");
    }

    @MessageMapping("/minion/buyArea")
    @SendTo("/topic/area")
    public void BuyArea(ActionOnHexGrid action) {

        int curPlayer = action.getCurPlayer();
        int row = action.getRow();
        int col = action.getCol();
        if(curPlayer == 1) {
            Player player = gameState.getPlayer1();
            player.buyArea(row,col, gameState.getGameMap() );
        }else{
            Player player = gameState.getPlayer2();
            player.buyArea(row,col, gameState.getGameMap());
        }
        System.out.println("buy area complete");
    }

    @MessageMapping("/minion/spawnMinion")
    @SendTo("/topic/minion")
    public void SpawnMinion(ActionOnHexGrid action) throws IOException {
        int curPlayer = action.getCurPlayer();
        int row = action.getRow();
        int col = action.getCol();
        String minionName = action.getMinion();

        if(curPlayer == 1) {
            Player player = gameState.getPlayer1();
            player.spawnMinion(player.getMinionByName(minionName),row,col);
        }
        else{
            Player player = gameState.getPlayer2();
            player.spawnMinion(player.getMinionByName(minionName),row,col);
        }
        System.out.println("spawn minion complete");
    }

    @MessageMapping("/minion/endTurn")
    @SendTo("/topic/executeMinion")
    public void ExecuteMinion(ActionOnHexGrid action) throws EvalError {
        int curPlayer = action.getCurPlayer();
        if(curPlayer == 1) {
            Player player = gameState.getPlayer1();
            GameState.executeMinion(player.getAllMinions());
            int current_turns = GameState.getCurrent_turns();
            player.calculateInterest(current_turns);
        }
        else{
            Player player = gameState.getPlayer2();
            GameState.executeMinion(player.getAllMinions());
            int current_turns = GameState.getCurrent_turns();
            player.calculateInterest(current_turns);
        }
        System.out.println("execute minion complete");
        messagingTemplate.convertAndSend("/topic/executeMinion", curPlayer);
    }
}
