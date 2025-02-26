package backend.KOMBOOD.config;

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
        int move_cost,
        int buy_minion_cost
) {
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

    public int buy_minion_cost() {
        return buy_minion_cost;
    }

}