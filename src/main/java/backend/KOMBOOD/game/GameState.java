package backend.KOMBOOD.game;

import backend.KOMBOOD.config.ConfigFile;
import backend.KOMBOOD.entity.Minion;
import backend.KOMBOOD.entity.MinionType;
import backend.KOMBOOD.entity.Player;
import backend.KOMBOOD.error.EvalError;
import backend.KOMBOOD.error.LexicalError;
import backend.KOMBOOD.map.MapMap;
import backend.KOMBOOD.parser.ExprTokenizer;
import backend.KOMBOOD.parser.StatementParser;
import backend.KOMBOOD.strategy.Strategy;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.util.Scanner;

public class GameState { // player1 and player2 can play in terminal and show gameMap while playing turn blablabla
    public static Player player1;
    public static Player player2;

    public static ArrayList<Minion> MinionOnMapMap = new ArrayList<>();

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

    //for debug
    private static Player currentPlayer = player1;

    //private Player currentPlayer;
    private String winner;

    private static Strategy p ;

    private MapMap gameMap;

    private List<MinionType> minionsShop = new ArrayList<MinionType>();

    public static int getCurrent_turns() {
        return current_turns;
    }

//    private ConfigFile config; // use this later

    // pre game

    //Choose Mode before GameState

    public GameState(Player player1, Player player2, MapMap gameMap) { // not done
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

    public void setup() throws LexicalError, EvalError, IOException { // numOfMinion , Minion, Hex/Map, budget
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
        player2.setArea(7, 8, gameMap);
        player2.setArea(8, 6, gameMap);
        player2.setArea(8, 7, gameMap);
        player2.setArea(8, 8, gameMap);

        //both player spawn first minion
        //minion setup
        setupMinion(minionsType);

        //initial budget
        player1.setBudget(init_budget);
        player2.setBudget(init_budget);

        gameMap.printMap();
        //Game Start
    }

    public void setupMinion(int amountMinions) throws LexicalError, EvalError, IOException {
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
            Strategy strategy = q.parse();


            MinionType minionType1 = new MinionType(name, def, strategy);
            Minion minion1 = new Minion(minionType1, init_hp, player1, gameMap);
            Minion minion2 = new Minion(minionType1, init_hp, player2, gameMap);

            MinionType.addMinionType(name, def, strategy);

            player1.addMinion(minion1);
            player2.addMinion(minion2);

            // for  Debug
            System.out.println("Minion created for Player1: " + minion1.getName());
            System.out.println("Minion created for Player2: " + minion2.getName());

//            player1.setMinion(minion1, 1, 1);
//            player2.setMinion(minion2,11,8);
        }
    }


    // while playing
    public void switchTurns() {
        this.currentPlayer = this.currentPlayer.equals(player2) ? player1 : player2;
        //this.current_turns++; // both player switch +1 turns
        // budget += budget * ดอกเบี้ย

    }

    public boolean checkWinner() {
        if (player1.getNumofMinion() == 0) {
            this.winner = "Player 2";
            return true;
        } else if (player2.getNumofMinion() == 0) {
            this.winner = "Player 1";
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
            return true;
        } else if (sumOfHp2 > sumOfHp1) {
            this.winner = "Player 2";
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
            this.winner = "Player1";
            return true;
        } else if (budget2 > budget1) {
            this.winner = "Player2";
            return true;
        } else {
            this.winner = "Draw!!!";
            return false;
        }
    }

    public void action(Player player) throws IOException, EvalError {
        Scanner s = new Scanner(System.in);
        String command = s.nextLine();
        while(command.equals("buy area") || command.equals("spawn minion")) {
            if (command.equals("buy area")) {
                int r = s.nextInt();
                int c = s.nextInt();
                s.nextLine();
                player.buyArea(r, c, gameMap);
//            } else if (command.equals("buy minion")) {
//                MinionType.displayMinionTypes();
//
//                System.out.println("Enter minion type to buy:");
//                String typeName = s.nextLine();
//                MinionType type = MinionType.getMinionType(typeName);
//
//                if (type != null) {
//                    Minion minion = new Minion(type, init_hp, player, gameMap);
//                    player.buyMinion(type, minion);
//                    System.out.println(player.getName() + " bought a " + type.getTypeName() + " minion.");
//                } else {
//                    System.out.println("Invalid minion type!");
//                }
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
                s.nextLine();
                player.spawnMinion(minion, r, c);
            }
            System.out.println(currentPlayer.getName() + " buy area, spawn minion");
            command = s.nextLine() ;
        }
    }

    public void executeMinion(ArrayList<Minion> minions) throws EvalError {
        int totalCost = 0;
        for (Minion minion : minions) {
            // check ว่า minion ถูก spawn แล้วหรือยัง
            if (minion.isSpawned()) {
                // for Debug
                System.out.println("Executing strategy for minion: " + minion.getName());

                Strategy strategy = minion.getType().getStrategy();
                strategy.evaluator(minion);

                totalCost += 1;
            } else {
                System.out.println("Minion " + minion.getName() + " has not been spawned yet!");
            }
        }
        currentPlayer.deductActionCost(totalCost);
    }

    //public void gameloop () throws LexicalError, EvalError { // not done

    public void gameloop() throws LexicalError, EvalError, IOException {
        while (current_turns <= max_turns) {
            // Player 1's turn
            System.out.println("Turn " + current_turns + ": " + player1.getName() + "'s turn");

            player1.calculateInterest(current_turns) ;

            // Player 1 Action: buy, spawn
            System.out.println(player1.getName() + " buy area, spawn minion");
            action(player1);
            gameMap.printMap();

            // Check Winner after Player 1's turn
            if (checkWinner()) {
                endGame();
                break;
            }

            player1.setHasNOTBoughtareaThisTurn() ;
            player1.setHasNOTSpawnedMinionThisTurn() ;

            // Switch to Player 2
            switchTurns();

            // Player 2's turn
            System.out.println("Turn " + current_turns + ": " + player2.getName() + "'s turn");

            player2.calculateInterest(current_turns) ;

            // Player 2 Action: buy, spawn
            System.out.println(player2.getName() + " buy area, spawn minion");
            action(player2);
            gameMap.printMap();

            // Execute Minions by Strategy
            executeMinion(MinionOnMapMap);
            gameMap.printMap();

            // Check Winner after Player 2's turn
            if (checkWinner()) {
                endGame();
                break;
            }

            player2.setHasNOTBoughtareaThisTurn() ;
            player2.setHasNOTSpawnedMinionThisTurn() ;

            // Increase turn count and switch back to Player 1
            current_turns++;
            //switchTurns();
        }
    }

        public void endGame () { // fixed
            if (checkWinner() || checkWinnerBySumOfHP() || checkWinnerByBudget()) {
                System.out.println(winner + " win!!!");
//            this.gameStatus = "End";
            }else if (!checkWinner()) {
                System.out.println(winner);
            }
        }

//    public void endTurn() {
//        // check มินเนี่ยนที่ไม่ทำอะไรเลย
//        for (Minion minion : currentPlayer.getMinion()) {
//            if (!minion.hasActedThisTurn()) {
//                currentPlayer.setBudget(currentPlayer.getBudget() - 1);
//                System.out.println("Minion " + minion.getName() + " did nothing this turn. Deducted 1 budget.");
//            }
//            minion.resetActionFlag(); // reset action ของมินเนี่ยน
//        }
//        switchTurns();
//    }
}
