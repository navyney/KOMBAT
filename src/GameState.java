import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameState { // player1 and player2 can play in terminal and show gameMap while playing turn blablabla
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

    private Player currentPlayer;
    private String winner;

    private Map gameMap;

    public static int getCurrent_turns() {
        return current_turns;
    }

//    private ConfigFile config; // use this later

    // pre game

    //Choose Mode before GameState

    public GameState(Player player1, Player player2, Map gameMap) { // not done
        this.player1 = player1;
        this.player2 = player2;
        this.gameMap = gameMap;
        this.currentPlayer = player1;
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

    public void setup() throws LexicalError, EvalError { // numOfMinion , Minion, Hex/Map, budget
        // choose amount type of minions
        // minion setup(name, type, def, strategy)
        // each player.area = 5 {(0,0) (0,1) (0,2) (1,0) (1,1)} {(6,6) (6,7) (7,5) (7,6) (7,7)}
        // both player.spawnAt their area
        // both player get budget

        // start turn 1
        System.out.println("How many minions type do you want to play?");// type of minion
        Scanner s = new Scanner(System.in);
        int minionsType = s.nextInt();

        //add their area
        player1.setArea(1, 1, gameMap);
        player1.setArea(1, 2, gameMap);
        player1.setArea(1, 3, gameMap);
        player1.setArea(2, 1, gameMap);
        player1.setArea(2, 2, gameMap);

        player2.setArea(7, 7, gameMap);
        player2.setArea(6, 8, gameMap);
        player2.setArea(8, 6, gameMap);
        player2.setArea(8, 7, gameMap);
        player2.setArea(8, 8, gameMap);

        //both player spawn first minion
        //minion setup
        setupMinion(minionsType);

        //initial budget
        player1.setBudget(init_budget);
        player2.setBudget(init_budget);

        //Game Start
    }

    public void setupMinion(int amountMinions) throws LexicalError, EvalError {
        Scanner s = new Scanner(System.in);

        for (int i = 0; i < amountMinions; i++) {
            System.out.println("Enter minion name:");
            String name = s.nextLine();

            System.out.println("Enter minion def:");
            int def = s.nextInt();
            s.nextLine();

            System.out.println("Enter minion strategy:");
            String strategyInput = s.nextLine();

            StatementParser q = new StatementParser(new ExprTokenizer(strategyInput));
            Strategy p = q.parse();


            MinionType minionType1 = new MinionType(name, def, p);
            Minion minion1 = new Minion(minionType1, init_hp, player1, gameMap);
            Minion minion2 = new Minion(minionType1, init_hp, player2, gameMap);
            player1.addMinion(minion1);
            player2.addMinion(minion2);
        }
    }


    // while playing
    public void switchTurns(String command) {
        if (command.equals("done")) {
            this.currentPlayer = this.currentPlayer.equals(player1) ? player1 : player2;
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

    public void action(Player player) {
        Scanner s = new Scanner(System.in);
        String command = s.nextLine();
        if (command.equals("buy area")) {
            int r = s.nextInt();
            int c = s.nextInt();
            player.buyArea(r, c, gameMap);
        } else if (command.equals("buy minion")) {
            MinionType.displayMinionTypes();

            System.out.println("Enter minion type to buy:");
            String typeName = s.nextLine();
            MinionType type = MinionType.getMinionType(typeName); // ดึงจาก HashMap

            if (type != null) {
                Minion minion = new Minion(type, init_hp, player, gameMap);
                player.buyMinion(type, minion);
                System.out.println(player.getName() + " bought a " + type.getTypeName() + " minion.");
            } else {
                System.out.println("Invalid minion type!");
            }
        } else if (command.equals("spawn minion")) {
            System.out.print("Your minions: ");
            for (Minion m : player.getMinion()) {
                System.out.print("Minion name: " + m.getName() + " ");
            }
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

    public void executeMinion(ArrayList<Minion> minions) throws EvalError {
        for (Minion minion : minions) {
            minion.getType().getStrategy().evaluator(minion);
        }
    }

    public void gameloop () throws LexicalError, EvalError { // not done

            // can for-loop until max_turns
//        while(gameStatus.equals("Playing")) {
//
//        }
            while (current_turns <= max_turns) {
                Player current = currentPlayer.equals(player1) ? player1 : player2;

                System.out.println("Turn " + current_turns + ": " + current.getName() + "'s turn");

                //Player Action buy, spawn
                System.out.println(current.getName() + "buy area, buy minion, spawn minion");
                action(current);

                //Execute Minions by Strategy
//                String strategy1 = "move downright";
//                ExprTokenizer E = new ExprTokenizer(strategy1);
//                StatementParser S = new StatementParser(E);
//                S.parse();
                executeMinion(current.getMinion());

                //Check Winner
                checkWinner();
            }
        }

        public void endGame (String winner) { // fixed
            if (checkWinner() || checkWinnerBySumOfHP() || checkWinnerByBudget()) {
                System.out.println(winner + " win!!!");
//            this.gameStatus = "End";
            }else if (!checkWinner()) {
                System.out.println(winner);
            }
        }
}
