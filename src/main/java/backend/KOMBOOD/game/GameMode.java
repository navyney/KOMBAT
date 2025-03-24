package backend.KOMBOOD.game;

public class GameMode{
    private GameModeType gameMode;


    public GameMode() {
        this.gameMode = GameModeType.DUEL;
    }

    public GameMode(GameModeType gameMode) {
        this.gameMode = gameMode;
        System.out.println("Game mode set to: " + this.gameMode);
    }

    public GameMode(String input) {
        GameModeType gameMode = GameModeType.valueOf(input);
    }

    public GameModeType getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameModeType gameMode) {
        this.gameMode = gameMode;
        System.out.println("Game mode set to: " + this.gameMode);
    }
}
