import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Player {
    String name;
    private double maxBudget;
    private double budget;
    private ArrayList<Minion> minion ;
    private ArrayList<Hex> area;
    private final ConfigFile config = Main.getConfig();
    private int spawnRemaining = config.max_spawns();
    //private GameState gameState;
    private int lastBuyAreaTurn = -1;
    private int lastBuyMinionTurn = -1;
    private int lastSpawnMinionTurn = -1;

    public Player(String name) {
        this.name = name;
        this.budget = config.init_budget();
        this.maxBudget = config.max_budget();
        this.minion = new ArrayList<Minion>();
        this.area = new ArrayList<Hex>();
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
        return this.spawnRemaining ;
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
        return GameState.getCurrent_turns() != lastBuyAreaTurn;
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

    public ArrayList<Minion> getMinion() {
        return minion;
    }

    public ArrayList<Hex> getArea() {
        return area;
    }

    public void buyMinion(MinionType type,Minion m) {
        if (!canBuyMinion()) {
            System.out.println("You already bought this turn. Wait for next turn!");
            return;
        }
        if (this.budget < config.buy_minion_cost()) {
            System.out.println("Not enough money to buy minion");
        } else {
            setBudget(this.getBudget() - config.buy_minion_cost()) ;
            addMinion(m);
            lastBuyMinionTurn = GameState.getCurrent_turns();
            System.out.println("Minion bought successfully");
        }
    }

    public void setArea(int r, int c, Map map) {
        HexHex hex = (HexHex) map.getHexAt(r, c);
        if(hex.owner() == 0){
            hex.setOwner(this.name.equals("Player1") ? 1 : 2);
            this.area.add(hex);
        }
    }

    public void buyArea(int r, int c, Map map) {
        if (!canBuyArea()) {
            System.out.println("You already bought this turn. Wait for next turn!");
            return;
        }
        // ตรวจสอบว่า Hex นั้นมีเจ้าของหรือไม่
        HexHex hex = (HexHex) map.getHexAt(r, c);
        // wall
        if (hex == null) {
            System.out.println("Invalid area!");
            return;
        }

        // no เจ้าของ
        if (hex.owner() == 0) {
            if (this.budget >= config.hex_purchase_cost()) {
                hex.setOwner(this.name.equals("Player1") ? 1 : 2);
                this.area.add(hex);
                setBudget(this.getBudget() - config.hex_purchase_cost()) ;
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

    public void spawnMinion(Minion minion, int r, int c) {
        if (!canSpawnMinion()) {
            System.out.println("You already bought this turn. Wait for next turn!");
            return;
        }
        // check that the Minion belong to this Player or not
        if (minion.getOwner() != this) {
            System.out.println("This minion does not belong to you!");
            return ;
        }

        if (this.getSpawnRemaining() == 0) {
            System.out.println("The number of minions has reached its limit.");
            return ;
        }

        if (this.budget < config.spawn_cost()) {
            System.out.println("Not enough budget to spawn minion!");
            return ;
        }

        // try to spawn Minion ในตำแหน่งที่กำหนด
        boolean success = minion.spawn(r, c);

        if (success) {
            setBudget(this.getBudget() - config.spawn_cost()) ;
            this.setSpawnRemaining();
            lastSpawnMinionTurn = GameState.getCurrent_turns();
            System.out.println("Minion " + minion.getName() + " spawned successfully at (" + r + "," + c + ")");
        } else {
            System.out.println("Failed to spawn minion at (" + r + "," + c + ")");
        }

    }

    public void calculateInterest() {
        double b = config.interest_pct(); // อัตราดอกเบี้ยฐาน
        double m = this.budget; // งบประมาณปัจจุบัน
        double t = GameState.getCurrent_turns(); // จำนวนเทิร์นปัจจุบัน
        double r ;
        double interest = 0 ;

        if (true) {
            if (m == 0 || m == 1) {
                m = 2 ;
                r = b * Math.log10(m) * Math.log(t);
            } else {
                // interest rate = b * log10(m) * ln(t)
                r = b * Math.log10(m) * Math.log(t);
            }
            m = this.budget ;
        }

        if (m==0) {
            m = 1 ;
            interest = m * (r / 100.0);
        } else {
            // interest = m * (r / 100)
            interest = m * (r / 100.0);
        }

        m = this.budget ;
        this.budget += interest + config.turn_budget() ;

        setBudget(Math.min(this.budget, config.max_budget())) ;

        System.out.println(this.name + " earned interest: " + (int)(interest) + ", new budget: " + getIntBudget());
    }

//    public void incrementTurnCount() {
//        this.turnCount++;
//    }

    // Getter สำหรับ budget
    public double getBudget() {
        return this.budget;
    }

    public int getIntBudget() {
        return (int)(this.budget);
    }

//    // dummy for testing
//    public int getTurnCount() {
//        return turnCount;
//    }

    public double setBudget(double budget) {
        return this.budget = budget;
    }

    //dummy done()
    public void done() {
        System.out.println(this.name + " has finished!");
    }

    //eiei

}
