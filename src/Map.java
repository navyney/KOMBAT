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

//    public void printMap() {
//        for (int i = 0; i < map.length; i++) {
//            for (int j = 0; j < map[i].length; j++) {
//                if (minions[i][j] != null) {
//                    System.out.print(minions[i][j].getOwner().getName() + " ");
//                } else {
//                    System.out.print(map[i][j] == 0 ? "X " : "- ");
//                }
//            }
//            System.out.println();
//        }
//    }

    public void printMap() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (minions[i][j] != null) {
                    // แสดง Minion ของผู้เล่น (I สำหรับ Player1, L สำหรับ Player2)
                    String ownerName = minions[i][j].getOwner().getName();
                    if (ownerName.equals("1")) {
                        System.out.print("I ");
                    } else if (ownerName.equals("2")) {
                        System.out.print("L ");
                    }
                } else if (map[i][j] == 0) {
                    // แสดงกำแพง (X)
                    System.out.print("X ");
                } else {
                    // ตรวจสอบว่า Hex นี้มีเจ้าของหรือไม่
                    HexHex hex = (HexHex) getHexAt(i + 1, j + 1); // แปลงจาก index 0-based เป็น 1-based
                    if (hex != null && hex.owner() != 0) {
                        // แสดงเจ้าของพื้นที่ (1 หรือ 2)
                        System.out.print(hex.owner() + " ");
                    } else {
                        // แสดงพื้นที่ว่าง (-)
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

//    //try spawn move and shoot (each player has only one minion for now)
//    public static void main(String[] args) {
//        Map gameMap = new Map(11, 8);
//        gameMap.createMap();
//
//
//        Player player1 = new Player("1");
//        Player player2 = new Player("2");
//
//        player1.buyArea(3, 3, gameMap);
//        player1.buyArea(2, 2, gameMap);
//
//        player2.buyArea(3, 1, gameMap);
//
//        MinionType warrior = new MinionType("Warrior", 0);
//
//        Minion minionP1 = new Minion("P1Minion", warrior, 10, player1, gameMap);
//        Minion minionP2 = new Minion("P2Minion", warrior, 10, player2, gameMap);
//
//        if (player1.spawnMinion(minionP1, 3, 3)) {
//            System.out.println("P1 Minion spawned at (3,3)");
//        }
//        if (player2.spawnMinion(minionP2,3, 1)) {
//            System.out.println("P2 Minion spawned at (3,1)");
//        }
//
//        System.out.println();
//        minionP1.move(6);
//        System.out.println(minionP1.getRow() + " ," + minionP1.getCol());
//
////        System.out.println();
////        minionP1.move(2);
////        System.out.println(minionP1.getRow() + " ," + minionP1.getCol());System.out.println();
////
////        System.out.println();
////        minionP1.move(1);
////        System.out.println(minionP1.getRow() + " ," + minionP1.getCol());System.out.println();
//
//        System.out.println();
//        gameMap.printMap();
//        System.out.println();
//        System.out.println("Player1 areas: " + player1.getNumofArea());
//        System.out.println("Player1 minions: " + player1.getNumofMinion());
//        System.out.println("Player2 areas: " + player2.getNumofArea());
//        System.out.println("Player2 minions: " + player2.getNumofMinion());
//
//        minionP1.shoot(5, 10);
//        System.out.println();
//
//        gameMap.printMap();
//        System.out.println();
//        System.out.println("Player1 areas: " + player1.getNumofArea());
//        System.out.println("Player1 minions: " + player1.getNumofMinion());
//        System.out.println("Player2 areas: " + player2.getNumofArea());
//        System.out.println("Player2 minions: " + player2.getNumofMinion());
//    }

//    public static void main(String[] args) {
//        Map gameMap = new Map(11, 8);
//        gameMap.createMap();
//
//        Player player1 = new Player("1", 5);
//        Player player2 = new Player("2", 5);
//
//        MinionType warrior = new MinionType("Warrior", 0);
//
//        Minion minionP1 = new Minion("P1Minion", warrior, 10, player1, gameMap);
//        Minion minionP2 = new Minion("P2Minion", warrior, 10, player2, gameMap);
//
//
//        player1.buyArea(3, 3, gameMap);
//        player1.buyArea(3, 3, gameMap);
//
//
//        player2.buyArea(3, 1, gameMap);
//        gameMap.printMap();
//
//        System.out.println();
//
//        if (minionP1.spawn(3, 3)) {
//            System.out.println("P1 Minion spawned at (3,3)");
//        }
//
//        if (minionP2.spawn(3, 1)) {
//            System.out.println("P2 Minion spawned at (3,1)");
//        }
//
//        if (minionP1.spawn(3, 1)) {
//            System.out.println("P1 Minion spawned at (3,1)");
//        }
//
//        System.out.println("Player1 areas: " + player1.getNumofArea());
//        System.out.println("Player1 minions: " + player1.getNumofMinion());
//        System.out.println("Player2 areas: " + player2.getNumofArea());
//        System.out.println("Player2 minions: " + player2.getNumofMinion());
//
//        gameMap.printMap();
//    }



}
