package backend.KOMBOOD.config;

import backend.WebSocketController.WebSocketController;
import backend.WebSocketDTOs.WebSocketDTO;
import jakarta.validation.constraints.Min;

public class Confi {
    public static int move_cost = 1;
    public static int spawn_cost;
    public static int hex_purchase_cost;
    public static int init_budget;
    public static int init_hp;
    public static int turn_budget;
    public static int max_budget;
    public static int interest_pct;
    public static int max_turns;
    public static int max_spawns;

    public Confi(@Min(0) int spawnedCost, @Min(0) int hexPurchasedCost, @Min(0) int initialBudget, @Min(0) int initialHP, @Min(0) int turnBudget, @Min(0) int maxBudget, @Min(0) int interestPercentage, @Min(0) int maxTurn, @Min(0) int maxSpawn, int i) {

    }

    public static void getSpawnCost() {
        System.out.println("Spawn cost = " + spawn_cost);
    }
    public static void getHexPurchasedCost() {
        System.out.println("Hex purchased cost = " + hex_purchase_cost);
    }
    public static void getInitBudget() {
        System.out.println("Initial budget = " + init_budget);
    }
    public static void getInit_hp() {
        System.out.println("Initial hp = " + init_hp);
    }
    public static void getTurnBudget() {
        System.out.println("Turn budget = " + turn_budget);
    }
    public static void getMaxBudget() {
        System.out.println("Max budget = " + max_budget);
    }
    public static void getInterestPercentage() {
        System.out.println("Interest percentage = " + interest_pct);
    }
    public static void getMaxTurn() {
        System.out.println("Max turn = " + max_turns);
    }
    public static void getMaxSpawn() {
        System.out.println("Max spawn = " + max_spawns);
    }
    public static void getMoveCost() {
        System.out.println("Move cost = " + move_cost);
    }

    public static void update(WebSocketDTO config) {
        spawn_cost = config.getSpawnedCost();
        hex_purchase_cost = config.getHexPurchasedCost();
        init_budget = config.getInitialBudget();
        init_hp = config.getInitialHP();
        turn_budget = config.getTurnBudget();
        max_budget = config.getMaxBudget();
        interest_pct = config.getInterestPercentage();
        max_turns = config.getMaxTurn();
        max_spawns = config.getMaxSpawn();
    }
    
    public int spawnedCost() {
        return spawn_cost;
    }
    public int hexPurchasedCost() {
        return hex_purchase_cost;
    }
    public int initBudget() {
        return init_budget;
    }
    public int initHp() {
        return init_hp;
    }   
    public int maxBudget() {
        return max_budget;
    }
    public int interestPercentage() {
        return interest_pct;
    }
    public int max_turns() {
        return max_turns;
    }
    public int maxSpawn() {
        return max_spawns;
    }
    public int moveCost() {
        return move_cost;
    }
    public int turnCost() {
        return turn_budget;
    }
}
