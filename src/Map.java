import java.util.*;

class Map {
    private int[][] map;
    private Minion[][] minions;
    private List<Hex> allHexes = new ArrayList<>();

//    private static ConfigFile config = new ConfigFile(
//            100, 100, 1000, 100,
//            90, 23456, 5, 69, 47);

    public Map(int row, int col) {
        this.map = new int[row][col];
        this.minions = new Minion[row][col];
    }

//    public static ConfigFile getConfig() {
//        return config;
//    }

    public void createMap() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if ((i == 0 && j >= 2 && j <= 7) || (i == 1 && j >= 4 && j <= 7) || (i == 2 && j >= 6 && j <= 7)) {
                    map[i][j] = 0;
                } else if((i == 8 && j <= 1) || (i == 9 && j <= 3) || (i == 10 && j <= 5)) {
                    map[i][j] = 0;
                } else {
                    map[i][j] = 1;
                }
                allHexes.add(new HexHex(i + 1, j + 1, false, 0));
            }
        }
    }

    public boolean isWall(int row, int col) {
        return map[row][col] == 0;
    }

    public boolean isMinionHere(int row, int col) {
        return minions[row][col] != null;
    }

    public void placeMinion(int row, int col, Minion minion) {
        minions[row][col] = minion;
    }

    public Minion getMinionAt(int row, int col) {
        return minions[row][col];
    }

    public void removeMinion(int row, int col) {
        minions[row][col] = null;
    }

    public Hex getHexAt(int row, int col) {
        for (Hex hex : allHexes) { // สมมติว่ามี List<Hex> allHexes ในคลาส Map
            if (hex.getRow() == row && hex.getCol() == col) {
                return hex;
            }
        }
        return null;
    }

    public void printMap() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (minions[i][j] != null) {
                    // show player's minion (I for Player1, L for Player2)
                    String ownerName = minions[i][j].getOwner().getName();
                    if (ownerName.equals("Player1")) {
                        System.out.print("I ");
                    } else if (ownerName.equals("Player2")) {
                        System.out.print("L ");
                    }
                } else if (map[i][j] == 0) {
                    // print wall as "X"
                    System.out.print("X ");
                } else {
                    // ตรวจสอบว่า Hex นี้มีเจ้าของหรือไม่
                    HexHex hex = (HexHex)getHexAt(i + 1, j + 1);
                    if (hex != null && hex.owner() != 0) {
                        // print owner
                        System.out.print(hex.owner() + " ");
                    } else {
                        // "-" is for free space
                        System.out.print("- ");
                    }
                }
            }
            System.out.println();
        }
    }

    public int getRows() {
        return map.length;
    }

    public int getCols() {
        return map[0].length;
    }

    public Map getGameMap() {
        return new Map(11, 8);
    }
}
