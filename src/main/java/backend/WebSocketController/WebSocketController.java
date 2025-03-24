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

import backend.KOMBOOD.config.Confi;
import java.lang.module.Configuration;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
    @Getter
    private static SetUpGameStage gameState;

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

        // 💡 Check if someone already selected pvb or bvb
        if (selectedMode != null && (selectedMode.equals("pvb") || selectedMode.equals("bvb"))) {
            // ❌ Reject any new players
            if (player1Id != null && !player1Id.equals(playerId)) {
                messagingTemplate.convertAndSendToUser(sessionId, "/topic/role-assigned", Map.of(
                        "role", "spectator",
                        "playerId", playerId,
                        "disableButtons", true
                ));
                messagingTemplate.convertAndSendToUser(sessionId, "/topic/lock-all", Map.of("locked", true));
                System.out.println("👀 Spectator rejected due to mode: " + selectedMode);
                return;
            }
        }

        // 🟢 Assign player1
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

            // ✅ Lock room immediately for PvB or BvB
            if (!"pvp".equals(mode)) {
                messagingTemplate.convertAndSend("/topic/lock-all", Map.of("locked", true));
                System.out.println("🔒 Room locked for 1-player mode");
            }
            return;
        }

        // 🟢 Assign player2 for PvP only
        if ("pvp".equals(selectedMode) && player2Id == null && !playerId.equals(player1Id)) {
            player2Id = playerId;

            messagingTemplate.convertAndSend("/topic/role-assigned", Map.of(
                    "role", "player2",
                    "playerId", playerId,
                    "disableButtons", true
            ));
            messagingTemplate.convertAndSend("/topic/lock-all", Map.of("locked", true));

            System.out.println("🎮 select-mode: " + playerId + " -> " + mode);
            return;
        }

        // ❌ Otherwise, assign as spectator
        messagingTemplate.convertAndSend("/topic/role-assigned", Map.of(
                "role", "spectator",
                "playerId", playerId,
                "disableButtons", true
        ));
        messagingTemplate.convertAndSend("/topic/lock-all", Map.of("locked", true));
        System.out.println("👀 Spectator rejected due to room full or invalid request");
    }

    @MessageMapping("/request-lock-status")
    public void handleRequestLockStatus(@Payload Map<String, String> payload, SimpMessageHeaderAccessor accessor) {
        String playerId = payload.get("playerId");

        if (selectedMode != null) {
            messagingTemplate.convertAndSend("/topic/lock-mode", Map.of("selectedMode", selectedMode));
            if (!"pvp".equals(selectedMode)) {
                messagingTemplate.convertAndSend("/topic/lock-all", Map.of("locked", true));
            }
        }

        if (player1Id != null && player2Id != null) {
            messagingTemplate.convertAndSend("/topic/lock-all", Map.of("locked", true));
        }
    }

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
        Confi.getSpawnCost();
        Confi.getHexPurchasedCost();
        Confi.getInitBudget();
        Confi.getInit_hp();
        Confi.getTurnBudget();
        Confi.getMaxBudget();
        Confi.getInterestPercentage();
        Confi.getMaxTurn();
        Confi.getMaxSpawn();
        Confi.getMoveCost();
    }

    @MessageMapping("/navigate")
    public void handleNavigation(@Payload String action) {
        if ("back".equals(action)) {
            System.out.println("🔁 Resetting game state and navigating back to select-mode");
            WebSocketController.clearPlayer1();
            WebSocketController.clearPlayer2();
            WebSocketController.resetGameState();
            messagingTemplate.convertAndSend("/topic/mode-reset", "reset");
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


    @MessageMapping("/gameState/setup")
    public void setUpGameStage(@Payload WebSocketDTO dto) throws LexicalError, EvalError, IOException {
        String id1 = dto.getPlayerId();
        String id2 = dto.getPlayerId();
        Player player1 = new Player("Player1",id1);
        Player player2 = new Player("Player2",id2);
        GameMode gameMode = new GameMode();
        gameMode.setGameMode(GameModeType.DUEL);
        gameState = new SetUpGameStage(player1, player2, gameMode);
        gameState.setUP();
        System.out.println("setup complete");
    }

    @MessageMapping("/minion/buyArea")
    @SendTo("/topic/area")
    public void BuyArea(ActionOnHexGrid action) {

        int curPlayer = action.getCurPlayer();
        int row = action.getRow();
        int col = action.getCol();
        if (curPlayer == 1) {
            Player player = gameState.getPlayer1();
            player.buyArea(row, col, gameState.getGameMap());
        } else {
            Player player = gameState.getPlayer2();
            player.buyArea(row, col, gameState.getGameMap());
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

        if (curPlayer == 1) {
            Player player = gameState.getPlayer1();
            player.spawnMinion(player.getMinionByName(minionName), row, col);
        } else {
            Player player = gameState.getPlayer2();
            player.spawnMinion(player.getMinionByName(minionName), row, col);
        }
        System.out.println("spawn minion complete");
    }

    @MessageMapping("/minion/endTurn")
    @SendTo("/topic/executeMinion")
    public void ExecuteMinion(ActionOnHexGrid action) throws EvalError {
        int curPlayer = action.getCurPlayer();
        if (curPlayer == 1) {
            Player player = gameState.getPlayer1();
            GameState.executeMinion(player);
            int current_turns = GameState.getCurrent_turns();
            player.calculateInterest(current_turns);
        } else {
            Player player = gameState.getPlayer2();
            GameState.executeMinion(player);
            int current_turns = GameState.getCurrent_turns();
            player.calculateInterest(current_turns);
        }
        System.out.println("execute minion complete");
        messagingTemplate.convertAndSend("/topic/executeMinion", curPlayer);
    }
        @MessageMapping("/request-current-config")
        @SendTo("/topic/config-update")
        public WebSocketDTO getCurrentConfig () {
            return currentConfig;
        }

        public static void resetGameState () {
            player1Id = null;
            player2Id = null;
            selectedMode = null;
            sessionPlayerMap.clear();
            sessionIds.clear();
            System.out.println("🔁 Game state has been fully reset.");
        }

        public static WebSocketDTO getCurrentConfigGame () {
            return currentConfig;

        }
    }

