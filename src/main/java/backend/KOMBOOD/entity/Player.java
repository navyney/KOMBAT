package backend.KOMBOOD.entity;

import backend.KOMBOOD.config.ConfigFile;
import backend.KOMBOOD.game.GameState;
import backend.KOMBOOD.map.Hex;
import backend.KOMBOOD.map.HexHex;
import backend.KOMBOOD.game.Main;
import backend.KOMBOOD.map.MapMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import static backend.KOMBOOD.game.GameState.player1;
import static backend.KOMBOOD.game.GameState.player2;

public class Player {
    String name;
    private double maxBudget;
    private double budget;
    private ArrayList<Minion> minion;
    private ArrayList<Minion> spawnedMinions;
    private ArrayList<Hex> area;
    private final ConfigFile config = Main.getConfig();
    private int spawnRemaining = config.max_spawns();
    private int lastBuyAreaTurn = -1;
    private int lastBuyMinionTurn = -1;
    private int lastSpawnMinionTurn = -1;
    private boolean hasBoughtAreaThisTurn = false;
    private boolean hasSpawnedMinionThisTurn = false;
    private String sessionId;
    private Random random = new Random();

    public Player(String name) {
        this.name = name;
        this.budget = config.init_budget();
        this.maxBudget = config.max_budget();
        this.minion = new ArrayList<Minion>();
        this.area = new ArrayList<Hex>();
        this.spawnedMinions = new ArrayList<>();
    }
    public Object get() {
        return this;
    }

    public void set(String sessionId) {
        this.sessionId = sessionId ;
    }


    public String getName() {
        return this.name;
    }

    public Minion getMinionByName(String name) {
        for (Minion m : minion) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }

    public int getSpawnRemaining() {
        //spawnRemaining ไม่น่าจะลบจาก จำนวนมินเนี่ยน ได้ เพราะผู้เล่นอาจจะแค่ซื้อมินเนี่ยนไว้แต่ไม่ได้ลง
        //this.spawnRemaining -= getNumofMinion();
        return this.spawnRemaining;
    }

    public void setSpawnRemaining() {
        this.spawnRemaining -= 1;
    }

    public int getNumofMinion() {
        return this.minion.size();
    }

    public int getNumofArea() {
        return this.area.size();
    }

    public double getMaxBudget() {
        return this.maxBudget;
    }

    public void setMaxBudget(double maxBudget) {
        this.maxBudget = maxBudget;
    }

    public boolean canBuyArea() {
        return GameState.getCurrent_turns() != lastBuyAreaTurn && !hasSpawnedMinionThisTurn;
    }

    public boolean canBuyMinion() {
        return GameState.getCurrent_turns() != lastBuyMinionTurn;
    }

    public boolean canSpawnMinion() {
        return GameState.getCurrent_turns() != lastSpawnMinionTurn;
    }

    public void addMinion(Minion m) {
//        if (this.getSpawnRemaining() == 0) {
//            System.out.println("The number of minions has reached its limit.");
//            return ;
//        }

        this.minion.add(m);
    }

    public void removeMinion(Minion m) {
        this.minion.remove(m);
    }

    public ArrayList<Minion> getMinion() {
        return minion;
    }

    public void addSpawnedMinion(Minion minion) {
        this.spawnedMinions.add(minion);
    }

    public ArrayList<Minion> getSpawnedMinions() {
        return this.spawnedMinions;
    }

    public ArrayList<Hex> getArea() {
        return area;
    }

    // no more buy minion
    public void buyMinion(MinionType type, Minion m) {
        if (!canBuyMinion()) {
            System.out.println("You already bought this turn. Wait for next turn!");
            return;
        }
        if (this.budget < config.buy_minion_cost()) {
            System.out.println("Not enough money to buy minion");
        } else {
            setBudget(this.getBudget() - config.buy_minion_cost());
            addMinion(m);
            lastBuyMinionTurn = GameState.getCurrent_turns();
            System.out.println("Minion " + m.getType().getTypeName() + " bought successfully");
        }
    }

    public void setArea(int r, int c, MapMap map) {
        HexHex hex = (HexHex) map.getHexAt(r, c);
        if (hex.owner() == 0) {
            hex.setOwner(this.name.equals("Player1") ? 1 : 2);
            this.area.add(hex);
        }
    }

    boolean isAdjacentToOwnedArea(int r, int c, MapMap map) {
//        int[] dr = {-1, -1, 0, 0, 1, 1};
//        int[] dc = {0, 1, -1, 1, -1, 0};
        //for hex index
        int[] dr = {-1, 0, 1, 1, 0, -1};
        int[] dc = {0, 1, 1, 0, -1, -1};

        for (int i = 0; i < 6; i++) {
            int newRow = r + dr[i];
            int newCol = c + dc[i];

            HexHex adjacentHex = (HexHex) map.getHexAt(newRow, newCol);

            if (adjacentHex != null && adjacentHex.owner() == (this.name.equals("Player1") ? 1 : 2)) {
                return true;
            }
        }
        return false;
    }

    public void deductActionCost(int cost) {
        if (this.budget >= cost) {
            this.budget -= cost;
        } else {
            System.out.println(this.name + " does not have enough budget to deduct " + cost + ".");
        }
    }

    public void buyArea(int r, int c, MapMap map) {
        if (!canBuyArea()) {
            System.out.println("You already action this turn. Wait for next turn!");
            return;
        }
        // ตรวจสอบว่า Hex นั้นมีเจ้าของหรือไม่
        HexHex hex = (HexHex) map.getHexAt(r, c);
        // wall
        if (hex == null) {
            throw new IllegalArgumentException("Invalid area!");
        }
        // no เจ้าของ
        if (hex.owner() == 0) {
            // check ว่าเป็น hex ที่ติดกับ hex ที่เรามีอยู่หรือไม่
            if (!isAdjacentToOwnedArea(r, c, map)) {
                System.out.println("You can only buy areas adjacent to your owned areas!");
                return;
            }
            if (this.budget >= config.hex_purchase_cost()) {
                hex.setOwner(this.name.equals("Player1") ? 1 : 2);
                this.area.add(hex);
                setBudget(this.getBudget() - config.hex_purchase_cost());
                hasBoughtAreaThisTurn = true;
                lastBuyAreaTurn = GameState.getCurrent_turns();
                System.out.println(this.name + " has bought area at (" + r + "," + c + ")");
            } else {
                System.out.println("Not enough budget to buy area!");
            }
        } else if (hex.owner() == 1) {
            System.out.println("Player1 already owns this area!");
        } else if (hex.owner() == 2) {
            System.out.println("Player2 already owns this area!");
        }
    }

    public void spawnMinion(Minion minion, int r, int c) throws IOException {
        boolean success ;
        if (!canSpawnMinion()) {
            System.out.println("You already action this turn. Wait for next turn!");
            return;
        }
        // check that the Minion belong to this Player or not
        if (minion.getOwner() != this) {
            System.out.println("This minion does not belong to you!");
            return;
        }

        Minion newMinion = new Minion(minion);

        // free for first turn
        if (GameState.getCurrent_turns() == 1) {
            success =  newMinion.spawn(r, c);

            if (success) {
                addSpawnedMinion(newMinion);
                if(GameState.getCurrentPlayer().equals(player1)){
                    player1.getAllMinions().add(newMinion);}
                else{
                    player2.getAllMinions().add(newMinion);
                }
                this.setSpawnRemaining();
                hasSpawnedMinionThisTurn = true;
                lastSpawnMinionTurn = GameState.getCurrent_turns();
                System.out.println("Minion " + newMinion.getName() + " spawned successfully at (" + r + "," + c + ")");
            } else {
                System.out.println("Failed to spawn minion at (" + r + "," + c + ")");
            }
            return;
        }

        if (this.budget < config.spawn_cost()) {
            System.out.println("Not enough budget to spawn minion!");
            return;
        }

        if (this.getSpawnRemaining() == 0) {
            System.out.println("The number of minions has reached its limit.");
            return;
        }

        // try to spawn Minion ในตำแหน่งที่กำหนด
        success = newMinion.spawn(r, c);

        if (success) {
            addSpawnedMinion(newMinion);
            if(GameState.getCurrentPlayer().equals(player1)){
                player1.getAllMinions().add(newMinion);}
            else{
                player2.getAllMinions().add(newMinion);
            }
            setBudget(this.getBudget() - config.spawn_cost());
            this.setSpawnRemaining();
            hasSpawnedMinionThisTurn = true;
            lastSpawnMinionTurn = GameState.getCurrent_turns();
            System.out.println("Minion " + newMinion.getName() + " spawned successfully at (" + r + "," + c + ")");
        } else {
            System.out.println("Failed to spawn minion at (" + r + "," + c + ")");
        }

    }

    public void calculateInterest(int t) {
        double b = config.interest_pct(); // อัตราดอกเบี้ยฐาน
        double m = this.budget; // งบประมาณปัจจุบัน
        double r;
        double interest = 0;

        if ( t == 1 ) {
            return ;
        }

        if (true) {
            if (m == 0 || m == 1) {
                m = 2;
                r = b * Math.log10(m) * Math.log(t);
            } else {
                // interest rate = b * log10(m) * ln(t)
                r = b * Math.log10(m) * Math.log(t);
            }
            m = this.budget;
        }

        if (m == 0) {
            m = 1;
            interest = m * (r / 100.0);
        } else {
            // interest = m * (r / 100)
            interest = m * (r / 100.0);
        }

        m = this.budget;
        this.budget += interest + config.turn_budget();

        setBudget(Math.min(this.budget, config.max_budget()));

        System.out.println(this.name + " earned interest: " + (int) (interest) + ", new budget: " + getIntBudget());
    }

//    public void incrementTurnCount() {
//        this.turnCount++;
//    }

    // Getter สำหรับ budget
    public double getBudget() {
        return this.budget;
    }

    public int getIntBudget() {
        return (int) (this.budget);
    }

    public double setBudget(double budget) {
        return this.budget = budget;
    }

    public boolean setHasNOTSpawnedMinionThisTurn() {
        return hasSpawnedMinionThisTurn = false;
    }

    public boolean setHasNOTBoughtareaThisTurn() {
        return hasBoughtAreaThisTurn = false ;
    }

    public ArrayList<Minion> getAllMinions() {
        return minion;
    }

    public ArrayList<Minion> getAllSpawnedMinion() {
        return spawnedMinions;
    }

    //dummy done()
    public void done() {
        System.out.println(this.name + " has finished!");
    }

    //eiei

//    why do we have setMinion() to spawn minion meanwhile we already have spawnMinion() the fuck?
//    public void setMinion(Minion minion, int r, int c) throws IOException {
//        if (minion.getOwner() != this) {
//            System.out.println("This minion does not belong to you!");
//            return;
//        }
//        // try to spawn Minion ในตำแหน่งที่กำหนด
//        boolean success = minion.spawn(r, c);
//
//        if (success) {
//            System.out.println("Minion " + minion.getName() + " spawned successfully at (" + r + "," + c + ")");
//        } else {
//            System.out.println("Failed to spawn minion at (" + r + "," + c + ")");
//        }
//    }

    //ส่วนของเปลี่ยน player เป็น bot
    public void takeTurn(MapMap map) throws IOException {
        ArrayList<Minion> minions = getAllMinions();
        System.out.println("Bot " + getName() + " is taking a turn...");
        int r = random.nextInt(1000);
        if(r % 10 == 0){

        }
        else if (r  % 3 == 0) {
            buyRandomArea(map);
        }else if (r % 3 == 1) {
            spawnRandomMinion(minions);
        }else{
            buyRandomArea(map);
            spawnRandomMinion(minions);
        }
        System.out.println("Bot " + getName() + " has finished its turn.");
    }

    private void buyRandomArea(MapMap map) {
        ArrayList<Hex> availableAreas = new ArrayList<>();

        for (int r = 0; r <= map.getRows(); r++) {
            for (int c = 0; c <= map.getCols(); c++) {
                HexHex hex = (HexHex) map.getHexAt(r, c);
                if (hex != null && hex.owner() == 0 && isAdjacentToOwnedArea(r, c, map)) {
                    availableAreas.add(hex);
                }
            }
        }

        if (!availableAreas.isEmpty()) {
            HexHex selectedHex = (HexHex) availableAreas.get(random.nextInt(availableAreas.size()));
            buyArea(selectedHex.getRow(), selectedHex.getCol(), map);
        }
    }

    private void buyRandomMinion(ArrayList<Minion> minions) {
        if (getBudget() < Main.getConfig().buy_minion_cost()) {
            return;
        }

        MinionType[] types = MinionType.getAllMinionTypes();

        if (types.length == 0) {
            return;
        }

        MinionType randomType = types[random.nextInt(types.length)];
        Minion minion = minions.get(random.nextInt(minions.size()));
        buyMinion(randomType, minion);
    }

    private void spawnRandomMinion(ArrayList<Minion> minions) throws IOException {
        if (getMinion().isEmpty() || getBudget() < Main.getConfig().spawn_cost()) {
            return;
        }

        Minion minion = minions.get(random.nextInt(minions.size()));

        ArrayList<Hex> ownedAreas = getArea();
        if (ownedAreas.isEmpty()) {
            return;
        }
        Hex selectedHex = ownedAreas.get(random.nextInt(ownedAreas.size()));
        spawnMinion(minion, selectedHex.getRow(), selectedHex.getCol());

    }
}
