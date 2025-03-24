package backend.KOMBOOD.config;

import backend.WebSocketController.WebSocketController;
import backend.WebSocketDTOs.WebSocketDTO;

public class Confi {
    static int spawn_cost = WebSocketController.getCurrentConfigGame().getSpawnedCost();
    static int hex_purchase_cost = WebSocketController.getCurrentConfigGame().getHexPurchasedCost();
    static int init_budget = WebSocketController.getCurrentConfigGame().getInitialBudget();
    static int init_hp = WebSocketController.getCurrentConfigGame().getInitialHP();
    static int turn_budget = WebSocketController.getCurrentConfigGame().getTurnBudget();
    static int max_budget = WebSocketController.getCurrentConfigGame().getMaxBudget();
    static int interest_pct = WebSocketController.getCurrentConfigGame().getInterestPercentage();
    static int max_turns = WebSocketController.getCurrentConfigGame().getMaxTurn();
    static int max_spawns = WebSocketController.getCurrentConfigGame().getMaxSpawn();
    static int move_cost = 1;

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
}
