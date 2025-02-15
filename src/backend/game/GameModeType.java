package backend.game;

public enum GameModeType {
    DUEL("Duel"),
    SOLITAIRE("Solitaire"),
    AUTO("Auto");

    private final String modeName;

    GameModeType(String modeName) {
        this.modeName = modeName;
    }

    public String getModeName() {
        return modeName;
    }

    public static GameModeType fromString(String text) {
        for (GameModeType mode : GameModeType.values()) {
            if (mode.modeName.equalsIgnoreCase(text)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Invalid game mode: " + text);
    }
}
