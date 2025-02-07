import java.io.IOException;
import java.util.Scanner;

public class GameState { // player1 and player2 can play in terminal and show map while playing turn blablabla
    private Player player1;
    private Player player2;

    private static int current_turns;
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
    private String winner;

    private Map map;

    public static int getCurrent_turns() {
        return current_turns;
    }

//    private ConfigFile config; // use this later

    // pre game

    //Choose Mode before GameState

    public GameState() { // not done
//        this.player1 = player1;
//        this.player2 = player2;
//        this.map = new Map(11, 8);
        this.current_turns = 1;
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
        //minion setup
        setupMinion(minionsType);

        //Player 2 choose minion

        //add their area
        player1.setArea(0, 0, map);
        player1.setArea(0, 1, map);
        player1.setArea(0, 2, map);
        player1.setArea(1, 0, map);
        player1.setArea(1, 1, map);

        player2.setArea(6, 6, map);
        player2.setArea(6, 7, map);
        player2.setArea(7, 5, map);
        player2.setArea(7, 6, map);
        player2.setArea(7, 7, map);

        //initial budget
        player1.setBudget(init_budget);
        player2.setBudget(init_budget);

        //Game Start
    }

    public void setupMinion(int amountMinions) {

        int minionsType = amountMinions;
        for (int i = 0; i < minionsType; i++) {
            Scanner s = new Scanner(System.in);
            minionsType = s.nextInt();
            System.out.println("Enter minion name:");
            String name = s.nextLine();

            System.out.println("Enter minion def:");
            int def = s.nextInt();

            s.nextLine();

            MinionType.addMinionType(name, def, null);

        }
    }


    // while playing
    public void switchTurns(String command) {
        if (command.equals("done")) {
            this.currentPlayer = this.currentPlayer.equals("Player 1") ? "Player 1" : "Player 2";
            this.current_turns++; // both player switch +1 turns
            // budget += budget * ดอกเบี้ย
        }
        ;
    }

    public boolean checkWinner() {
        if (player1.getNumofMinion() == 0) {
            this.winner = "Player 2";
            endGame(winner);
            return true;
        } else if (player2.getNumofMinion() == 0) {
            this.winner = "Player 1";
            endGame(winner);
            return true;
        } else if (this.current_turns == this.max_turns && player1.getNumofMinion() == player2.getNumofMinion()) {
            checkWinnerBySumOfHP();
            return false;
        }
        return false;
    }

    public int getTotalMinionsHp(Player player) {
        return player.getMinion().stream().mapToInt(Minion::getHp).sum();
    }

    public boolean checkWinnerBySumOfHP() {
        int sumOfHp1 = getTotalMinionsHp(player1);
        int sumOfHp2 = getTotalMinionsHp(player2);

        if (sumOfHp1 > sumOfHp2) {
            this.winner = "Player 1";
            endGame(winner);
            return true;
        } else if (sumOfHp2 > sumOfHp1) {
            this.winner = "Player 2";
            endGame(winner);
            return true;
        } else if (this.current_turns == this.max_turns && sumOfHp1 == sumOfHp2) {
            checkWinnerByBudget();
            return false;
        } else return false;
    }

    public boolean checkWinnerByBudget() {
        int budget1 = player1.getIntBudget();
        int budget2 = player2.getIntBudget();
        if (budget1 > budget2) {
            this.winner = "Player 1";
            endGame(winner);
            return true;
        } else if (budget2 > budget1) {
            this.winner = "Player 2";
            endGame(winner);
            return true;
        } else {
            this.winner = "Draw!!!";
            endGame(winner);
            return false;
        }
    }

    public void action(Player player) throws IOException {
        Scanner s = new Scanner(System.in);
        String command = s.nextLine();
        if (command.equals("buy area")) {
            int r = s.nextInt();
            int c = s.nextInt();
            player.buyArea(r, c, map);
        } else if (command.equals("buy minion")) {
            MinionType.displayMinionTypes();

            System.out.println("Enter minion type to buy:");
            String typeName = s.nextLine();
            MinionType type = MinionType.getMinionType(typeName); // ดึงจาก HashMap

            if (type != null) {
                Minion minion = new Minion(type, init_hp, player, map);
                player.buyMinion(type, minion);
                System.out.println(player.getName() + " bought a " + type.getTypeName() + " minion.");
            } else {
                System.out.println("Invalid minion type!");
            }
        } else if (command.equals("spawn minion")) {
            System.out.println("Your minions: " + player.getMinion());
            System.out.println("Enter minion name to spawn:");
            String minionName = s.nextLine();

            Minion minion = player.getMinionByName(minionName);
            if (minion == null) {
                System.out.println("You don't have this minion!");
                return;
            }

            System.out.println("Enter spawn location (row column):");
            int r = s.nextInt();
            int c = s.nextInt();

            player.spawnMinion(minion, r, c);
        } else if (command.equals("done")) {
            switchTurns("done");
        }
    }

        public void gameloop () throws LexicalError, EvalError, IOException { // not done

            // can for-loop until max_turns
//        while(gameStatus.equals("Playing")) {
//
//        }
            while (current_turns <= max_turns) {
                Player current = currentPlayer.equals("Player 1") ? player1 : player2;

                System.out.println("Turn " + current_turns + ": " + current.getName() + "'s turn");

                //Player Action buy, spawn
                action(current);

                //Execute Minions by Strategy
                String strategy1 = "move downright";
                ExprTokenizer E = new ExprTokenizer(strategy1);
                StatementParser S = new StatementParser(E);
                S.parse();

                //Check Winner
                checkWinner();
            }
        }

        // post game
        public void endGame (String winner) { // fixed
            if (checkWinner() || checkWinnerBySumOfHP() || checkWinnerByBudget()) {
                System.out.println(winner + " win!!!");
//            this.gameStatus = "End";
            }else if (!checkWinner()) {
                System.out.println(winner);
            }
        }
}
