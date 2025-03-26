package backend.WebSocketController;


import backend.KOMBOOD.config.ConfigFile;
import backend.KOMBOOD.game.*;
import backend.WebSocketDTOs.MinionConfigMessage;
import backend.KOMBOOD.entity.Player;
import backend.KOMBOOD.error.EvalError;
import backend.KOMBOOD.error.LexicalError;
import backend.WebSocketDTOs.ActionOnHexGrid;
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
import java.io.IOException;
import backend.KOMBOOD.config.Confi;
import java.lang.module.Configuration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import backend.WebSocketDTOs.MinionType;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private static WebSocketDTO currentConfig;

    @Getter
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
    private static final Map<String, String> sessionPlayerMap = new ConcurrentHashMap<>();
    @Getter
    private static SetUpGameStage gameState;

    @MessageMapping("/join-game")
    public void handleJoinGame(@Payload Map<String, String> payload, SimpMessageHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        String playerId = payload.get("playerId");

        // use set, so the same id won't be count yeyey
        sessionIds.add(sessionId);
        sessionPlayerMap.put(sessionId, playerId);
        System.out.println("✅ Player connected: " + playerId);

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
            messagingTemplate.convertAndSendToUser(sessionId, "/topic/lock-mode", Map.of("selectedMode", selectedMode));
        }
    }

    @MessageMapping("/select-mode")
    public void handleSelectMode(@Payload Map<String, String> payload, SimpMessageHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        String playerId = payload.get("playerId");
        String mode = payload.get("mode");

        if (selectedMode != null && (selectedMode.equals("pvb") || selectedMode.equals("bvb"))) {

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
            System.out.println("✅ Assigned role: player1");

            if (!"pvp".equals(mode)) {
                messagingTemplate.convertAndSend("/topic/lock-all", Map.of("locked", true));
                System.out.println("🔒 Room locked for 1-player mode");
            }
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
            System.out.println("✅ Assigned role: player2");
            return;
        }

        messagingTemplate.convertAndSend("/topic/role-assigned", Map.of(
                "role", "spectator",
                "playerId", playerId,
                "disableButtons", true
        ));
        messagingTemplate.convertAndSend("/topic/lock-all", Map.of("locked", true));
        System.out.println("👀 Spectator rejected due to room full");
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

        if (!sessionPlayerMap.containsKey(sessionId)) {
            sessionIds.add(sessionId);
            sessionPlayerMap.put(sessionId, playerId);
        }

        int count = 0;
        if (player1Id != null) count++;
        if (player2Id != null) count++;

        messagingTemplate.convertAndSend("/topic/player-count", count);
        System.out.println("👥 Players in Config-set-up (real players): " + count);
    }

    @MessageMapping("/config-update")
    public void handleConfigUpdate(@Payload WebSocketDTO config) {
        currentConfig = config;
        Confi.update(config);
//        ConfigFile configg = new ConfigFile(
//                config.getSpawnedCost(),config.getHexPurchasedCost(),config.getInitialBudget(),config.getInitialHP(),config.getTurnBudget()
//                ,config.getMaxBudget(),config.getInterestPercentage(),config.getMaxTurn(),config.getMaxSpawn(),1);
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
        //ConfigFile.getAllConFig();
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
        Confi configFile = new Confi(
                currentConfig.getSpawnedCost(),
                currentConfig.getHexPurchasedCost(),
                currentConfig.getInitialBudget(),
                currentConfig.getInitialHP(),
                currentConfig.getTurnBudget(),
                currentConfig.getMaxBudget(),
                currentConfig.getInterestPercentage(),
                currentConfig.getMaxTurn(),
                currentConfig.getMaxSpawn(),
                1 // moveCost
        );
        gameState.getGameState().setConfig(configFile);
        messagingTemplate.convertAndSend("/gamestate/config", currentConfig);
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
        gameState.getGameMap().printMap();
    }

    @MessageMapping("/minion-config")
    public void handleMinionConfig(@Payload MinionConfigMessage message, Message<?> rawMessage) {
        String playerId = message.getPlayerId();
        List<MinionType> minions = message.getMinions();

        System.out.println("🧠 got Minion config from playerId: " + playerId);

        if (minions == null || minions.isEmpty()) {
            System.out.println("⚠️ Not found Minions that have send");
            return;
        }

        for (MinionType minion : minions) {
            System.out.println("➡️ Minion ID: " + minion.getId());
            System.out.println("   Name     : " + minion.getName());
            System.out.println("   DEF      : " + minion.getDef());
            System.out.println("   Strategy : " + minion.getStrategy());
            System.out.println();
        }

        messagingTemplate.convertAndSend("/topic/minion-updated", message);
    }

    @MessageMapping("/join-select-minion-type")
    public void handleJoinSelectMinionType(@Payload Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String playerId = payload.get("playerId");

        if (!sessionPlayerMap.containsKey(sessionId)) {
            sessionIds.add(sessionId);
            sessionPlayerMap.put(sessionId, playerId);
        }

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

    @MessageMapping("/minion-update")
    public void handleMinionUpdate(@Payload MinionConfigMessage message) {
        String playerId = message.getPlayerId();
        List<MinionType> updatedMinions = message.getMinions();

        if (updatedMinions == null || updatedMinions.isEmpty()) {
            System.out.println("⚠️ No data of minion that send to /minion-update");
            return;
        }

        System.out.println("🛠️ update minion from playerId: " + playerId);
        for (MinionType minion : updatedMinions) {
            System.out.println(" Minion ID: " + minion.getId());
            System.out.println("   Name     : " + minion.getName());
            System.out.println("   DEF      : " + minion.getDef());
            System.out.println("   Strategy : " + minion.getStrategy());
        }

        messagingTemplate.convertAndSend("/topic/minion-updated", message);
    }

    public static WebSocketDTO getCurrentConfigGame() {
        return currentConfig;
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
        gameState.getGameMap().printMap();
    }

    @MessageMapping("/minion/endTurn")
    @SendTo("/topic/executeMinion")
    public void ExecuteMinion(ActionOnHexGrid action) throws EvalError {
        int curPlayer = action.getCurPlayer();
        int current_turns = GameStateWithFrontEnd.getCurrent_turns();
        if (curPlayer == 1) {
            Player player = gameState.getPlayer1();
            player.calculateInterest(current_turns);
            GameStateWithFrontEnd.executeMinion(player);
            player.setHasNOTBoughtareaThisTurn();
            player.setHasNOTSpawnedMinionThisTurn();
            gameState.getGameState().switchTurns();
        } else {
            Player player = gameState.getPlayer2();
            player.calculateInterest(current_turns);
            GameStateWithFrontEnd.executeMinion(player);
            gameState.getGameState().switchTurns();
            player.setHasNOTBoughtareaThisTurn();
            player.setHasNOTSpawnedMinionThisTurn();
            GameStateWithFrontEnd.setCurrent_turns(current_turns+1);
        }
        System.out.println("execute minion complete");
        gameState.getGameMap().printMap();
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

    }

