package backend.KOMBOOD.game;
import backend.KOMBOOD.entity.Minion;
import backend.KOMBOOD.entity.MinionType;
import backend.KOMBOOD.entity.Player;
import backend.KOMBOOD.error.EvalError;
import backend.KOMBOOD.error.LexicalError;
import backend.KOMBOOD.map.MapMap;
import backend.KOMBOOD.strategy.Strategy;

import java.io.IOException;
import java.util.ArrayList;

public class SetUpGameStage {
    MapMap gameMap;
    Player player1;
    Player player2;
    GameMode gameMode;
    GameModeType gameModeType;
    Strategy strategy;
    MinionType minionType = new MinionType("dummy",1,strategy);
    Minion minion = new Minion(minionType,100,player1,gameMap);
    GameStateWithFrontEnd gameState;
    public SetUpGameStage(Player player1, Player player2, GameMode gameMode) {
        this.player1 = player1;
        this.player2 = player2;
        this.gameMode = gameMode;

    }
    public void setUP() throws LexicalError, EvalError, IOException {
        gameMap = new MapMap(8, 8);
        gameModeType = gameMode.getGameMode();
        gameState = new GameStateWithFrontEnd(player1, player2, gameMap, gameModeType);
        gameMap.createMap();
        //gameState.setConfig(); bug ngae ๆ
        gameState.setup();
        gameState.setupMinion(minion,"move up");
    }
    public Player getPlayer1() {
        return player1;
    }
    public Player getPlayer2() {
        return player2;
    }
    public GameMode getGameMode() {
        return gameMode;
    }
    public MapMap getGameMap() {
        return gameMap;
    }
    public GameStateWithFrontEnd getGameState() {return gameState;}
}
