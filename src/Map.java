class Map {
    private int[][] map;
    private Minion[][] minions;

    public Map(int row, int col) {
        this.map = new int[row][col];
        this.minions = new Minion[row][col];
    }

    public void createMap() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if ((i == 0 && j >= 2 && j <= 7) || (i == 1 && j >= 4 && j <= 7) || (i == 2 && j >= 6 && j <= 7)) {
                    map[i][j] = 0;
                } else if((i == 8 && j <= 1) || (i == 9 && j <= 3) || (i == 10 && j <= 5)) {
                    map[i][j] = 0;
                } else map[i][j] = 1;
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

    public void printMap() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (minions[i][j] != null) {
                    System.out.print(minions[i][j].getOwner().getName() + " ");
                } else {
                    System.out.print(map[i][j] == 0 ? "X " : "- ");
                }
            }
            System.out.println();
        }
    }

    //try spawn move and shoot (each player has only one minion for now)
    public static void main(String[] args) {
        Map gameMap = new Map(11, 8);
        gameMap.createMap();

        Player player1 = new Player("1");
        Player player2 = new Player("2");

        MinionType warrior = new MinionType("Warrior", 0);

        Minion minionP1 = new Minion("P1Minion", warrior, 10, player1);
        Minion minionP2 = new Minion("P2Minion", warrior, 10, player2);

        if (minionP1.spawn(3, 3, gameMap)) {
            System.out.println("P1 Minion spawned at (3,3)");
        }
        if (minionP2.spawn(3, 1, gameMap)) {
            System.out.println("P2 Minion spawned at (3,1)");
        }

        System.out.println();
        minionP1.move("upleft", gameMap);
        System.out.println(minionP1.getRow() + " ," + minionP1.getCol());System.out.println();


        gameMap.printMap();

        minionP1.shoot(3, 1, gameMap,10);

        gameMap.printMap();
    }
}
