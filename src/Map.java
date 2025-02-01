class Map {
    private int[][] map;
    private boolean[][] occupied;

    public Map(int row, int col) {
        this.map = new int[row][col];
        this.occupied = new boolean[row][col];
    }

    public void createMap() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if ((i == 0 && j >= 1 && j <= 7) || (i == 1 && j >= 3 && j <= 7) || (i == 2 && j >= 5 && j <= 7) || (i == 3 && j == 7)) {
                    map[i][j] = 0;
                } else if((i == 8 && j == 0) || (i == 9 && j <= 2) || (i == 10 && j <= 4) || (i == 11 && j <= 6)) {
                    map[i][j] = 0;
                } else {
                    map[i][j] = 1;
                }
            }
        }
    }

    public boolean isWall(int row, int col) {
        return map[row][col] == 0;
    }

    public boolean isMinionHere(int row, int col) {
        return occupied[row][col];
    }

    public void placeMinion(int row, int col) {
        occupied[row][col] = true;
    }

    public void printMap() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (occupied[i][j]) {
                    System.out.print("M ");
                } else {
                    System.out.print(map[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Map m = new Map(11, 8);
        m.createMap();

        MinionType warrior = new MinionType("Warrior", 5);
        Minion minion = new Minion("Minion1", warrior, 100);

        if (minion.spawn(5, 5, m)) {
            m.placeMinion(5, 5);
            System.out.println("Minion spawned at (5,5)");
        } else {
            System.out.println("Failed to spawn minion at (5,5)");
        }

        m.printMap();
        System.out.println();
        minion.move("up");
        System.out.println(minion.getRow() + " ," + minion.getCol());
        System.out.println();
        minion.move("upleft");
        System.out.println(minion.getRow() + " ," + minion.getCol());System.out.println();
        minion.move("down");
        System.out.println(minion.getRow() + " ," + minion.getCol());System.out.println();
        minion.move("downleft");
        System.out.println(minion.getRow() + " ," + minion.getCol());System.out.println();
        minion.move("upright");
        System.out.println(minion.getRow() + " ," + minion.getCol());System.out.println();
        minion.move("downright");
        System.out.println(minion.getRow() + " ," + minion.getCol());
    }
}
