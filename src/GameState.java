import java.util.Scanner;

public class GameState extends GameMode{ // player1 and player2 can play in terminal and show map while playing turn blablabla
    private Player player1;
    private Player player2;

    private int current_turns;
    private int max_turns;
    private int spawn_cost;
    private int hex_purchase_cost;
    private int init_budget;
    private int init_hp;
    private int turn_budget;
    private int max_budget;
    private int interest_pct;
    private int max_spawns;

    private String currentPlayer;
//    private String gameStatus; // playing, ended
    private String winner;

    private Map map;

//    private ConfigFile config; // use this later

    // pre game
    private static ConfigFile config = new ConfigFile(
            100, 100, 1000, 100,
            90, 23456, 5, 69, 47);

    public static ConfigFile getConfig() {
        return config;
    }

    //Choose Mode before GameState

    public GameState(Player player1, Player player2) { // not done
        this.player1 = player1;
        this.player2 = player2;
        this.map = new Map(11,8);
    }

    public void setConfig(ConfigFile config) { // maybe need to add config file here, not done
        this.max_turns = config.max_turns();
        this.spawn_cost = config.spawn_cost();
        this.hex_purchase_cost = config.hex_purchase_cost();
        this.init_budget = config.init_budget();
        this.init_hp = config.init_hp();
        this.turn_budget = config.turn_budget();
        this.max_budget = config.max_budget();
        this.interest_pct = config.interest_pct();
        this.max_spawns = config.max_spawns();
    }

    public void setup() { // numOfMinion , Minion, Hex/Map, budget
        // choose amount type of minions
        // minion setup(name, type, def, strategy)
        // each player.area = 5 {(0,0) (0,1) (0,2) (1,0) (1,1)} {(6,6) (6,7) (7,5) (7,6) (7,7)}
        // both player.spawnAt their area
        // both player get budget

        // start turn 1
        System.out.println("How many minions type do you want to play?");// type of minion
        Scanner s = new Scanner(System.in);
        int minionsType = s.nextInt();

        //player 1 choose minion
        for (int i = 0; i < minionsType; i++) { //minion setup
            System.out.println("Enter minion name:");
            String name = s.nextLine();

            System.out.println("Enter minion def:");
            int def = s.nextInt();

            s.nextLine();

            MinionType type = new MinionType(name, def);
            Minion minion = new Minion(name, type, init_hp, player1, map);
            player1.addMinion(minion);
        }
        System.out.println(player1 + "minions: " + player1.getMinion());

        //Player 2 choose minion

        player1.setArea(0,0,map); //add their area
        player1.setArea(0,1,map);
        player1.setArea(0,2,map);
        player1.setArea(1,0,map);
        player1.setArea(1,1,map);

        player2.setArea(6,6,map);
        player2.setArea(6,7,map);
        player2.setArea(7,5,map);
        player2.setArea(7,6,map);
        player2.setArea(7,7,map);

        player1.setBudget(init_budget); //initial budget
        player2.setBudget(init_budget);

        //Game Start
    }

    // while playing
    public void switchTurns(String command) {
        if (command.equals("done")) {
            this.currentPlayer = this.currentPlayer.equals("Player 1") ? "Player 1" : "Player 2";
            this.current_turns++; // both player switch +1 turns
            // budget += budget * ดอกเบี้ย
        };
    }

    public boolean checkWinner() {
        if (player1.getNumofMinion() == 0) {
            this.winner = "Player 2 Wins!";
            return true;
        }else if (player2.getNumofMinion() == 0) {
            this.winner = "Player 1 Wins!";
            return true;
        } else if (this.current_turns == this.max_turns && player1.getNumofMinion() == player2.getNumofMinion()) {
            checkWinnerBySumOfHP();
            return false;
        }return false;
    }

    public int getTotalMinionsHp(Player player) {
        return player.getMinion().stream().mapToInt(Minion::getHp).sum();
    }

    public boolean checkWinnerBySumOfHP() {
        int sumOfHp1 = getTotalMinionsHp(player1);
        int sumOfHp2 = getTotalMinionsHp(player2);

        if (sumOfHp1 > sumOfHp2) {
            this.winner = "Player 1 Wins!";
            return true;
        }else if (sumOfHp2 > sumOfHp1) {
            this.winner = "Player 2 Wins!";
            return true;
        }else if (this.current_turns == this.max_turns && sumOfHp1 == sumOfHp2) {
            checkWinnerByBudget();
            return false;
        }else return false;
    }

    public void checkWinnerByBudget() {
        int budget1 = player1.getIntBudget();
        int budget2 = player2.getIntBudget();
        if (budget1 > budget2) {
            this.winner = "Player 1 Wins!";
        }else if (budget2 > budget1) {
            this.winner = "Player 2 Wins!";
        }else winner = "Draw";
    }

    public void gameloop() { // not done

        // can for-loop until max_turns
//        while(gameStatus.equals("Playing")) {
//
//        }

        for (int i = 1;     i <= max_turns; i++) {
            //
        }
    }

    // post game
    public void endGame() { // fixed
        if (checkWinner() || checkWinnerBySumOfHP() || checkWinner()) {
            System.out.println(winner);
//            this.gameStatus = "End";
        }
    }
}
