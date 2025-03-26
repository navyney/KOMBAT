package backend.KOMBOOD.config;

import backend.WebSocketDTOs.WebSocketDTO;

public record ConfigFile(
        int spawn_cost,
        int hex_purchase_cost,
        int init_budget,
        int init_hp,
        int turn_budget,
        int max_budget,
        int interest_pct,
        int max_turns,
        int max_spawns,
        int move_cost
) {
//    public void update(WebSocketDTO config) {
//        spawn_cost = config.getSpawnedCost();
//        hex_purchase_cost = config.getHexPurchasedCost();
//        init_budget = config.getInitialBudget();
//        init_hp = config.getInitialHP();
//        turn_budget = config.getTurnBudget();
//        max_budget = config.getMaxBudget();
//        interest_pct = config.getInterestPercentage();
//        max_turns = config.getMaxTurn();
//        max_spawns = config.getMaxSpawn();
//    }

    public int spawn_cost() {
        return spawn_cost;
    }

    public int hex_purchase_cost() {
        return hex_purchase_cost;
    }

    public int init_budget() {
        return init_budget;
    }

    public int init_hp() {
        return init_hp;
    }

    public int turn_budget() {
        return turn_budget;
    }

    public int max_budget() {
        return max_budget;
    }

    public int interest_pct() {
        return interest_pct;
    }

    public int max_turns() {
        return max_turns;
    }

    public int max_spawns() {
        return max_spawns;
    }

    public int move_cost() {
        return move_cost;
    }

    public void getSpawnCost() {
        System.out.println("Spawn cost = " + spawn_cost());
    }
    public void getHexPurchasedCost() {
        System.out.println("Hex purchased cost = " + hex_purchase_cost());
    }
    public void getInitBudget() {
        System.out.println("Initial budget = " + init_budget());
    }
    public void getInit_hp() {
        System.out.println("Initial hp = " + init_hp());
    }
    public void getTurnBudget() {
        System.out.println("Turn budget = " + turn_budget());
    }
    public void getMaxBudget() {
        System.out.println("Max budget = " + max_budget());
    }
    public void getInterestPercentage() {
        System.out.println("Interest percentage = " + interest_pct());
    }
    public void getMaxTurn() {
        System.out.println("Max turn = " + max_turns());
    }
    public void getMaxSpawn() {
        System.out.println("Max spawn = " + max_spawns());
    }
    public void getMoveCost() {
        System.out.println("Move cost = " + move_cost());
    }

//    public static void getAllConFig() {
//        getSpawnCost();
//        getHexPurchasedCost();
//        getInitBudget();
//        getInit_hp();
//        getTurnBudget();
//        getMaxBudget();
//        getInterestPercentage();
//        getMaxTurn();
//        getMaxSpawn();
//        getMoveCost();
//    }

}