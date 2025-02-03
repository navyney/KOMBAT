import java.util.ArrayList;

public class Player {
    String name;
    //    String type;
    private double maxBudget;
    private double budget;
    private ArrayList<Minion> minion = new ArrayList<>();
    private ArrayList<Hex> area;
    private ConfigFile config = Map.getConfig();
    private int turnCount = 0;

    public Player(String name) {
        //        this.type = type;
        this.name = name;
        this.budget = config.init_budget();
        this.maxBudget = config.max_budget();
        this.minion = new ArrayList<Minion>();
        this.area = new ArrayList<Hex>();
    }

    public String getName() {
        return this.name;
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

    public void addMinion(Minion m) {
        this.minion.add(m);
    }

    public ArrayList<Minion> getMinion() {
        return minion;
    }

    public ArrayList<Hex> getArea() {
        return area;
    }

//    public void buyArea(int r, int c) {
//        // อย่าลืมเช็คเงินก่อน buy และหักเงินหลัง buy
//        Hex h = new HexHex(r,c,false,1);
//        area.add(h);
//    }

    public void buyArea(int r, int c, Map map) {
        // ตรวจสอบว่า Hex นั้นมีเจ้าของหรือไม่
        HexHex hex = (HexHex) map.getHexAt(r, c);
        if (hex == null) {
            System.out.println("Invalid area!");
            return;
        }

        if (hex.owner() == 0) {
            if (this.budget >= config.hex_purchase_cost()) {
                hex.setOwner(this.name.equals("1") ? 1 : 2);
                this.area.add(hex);
                this.budget -= config.hex_purchase_cost();
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

    public boolean spawnMinion(Minion minion, int r, int c) {
        // check that the Minion belong to this Player or not
        if (minion.getOwner() != this) {
            System.out.println("This minion does not belong to you!");
            return false;
        }

        if (this.budget < config.spawn_cost()) {
            System.out.println("Not enough budget to spawn minion!");
            return false;
        }

        // try to spawn Minion ในตำแหน่งที่กำหนด
        boolean success = minion.spawn(r, c);

        if (success) {
            this.budget -= config.spawn_cost();
            System.out.println("Minion " + minion.getName() + " spawned successfully at (" + r + "," + c + ")");
        } else {
            System.out.println("Failed to spawn minion at (" + r + "," + c + ")");
        }

        return success;
    }

    public void addTurnBudget() {
        this.budget += config.turn_budget();
        this.budget = Math.min(this.budget, config.max_budget());
    }

    public void calculateInterest() {
        double b = config.interest_pct(); // อัตราดอกเบี้ยฐาน
        double m = this.budget; // งบประมาณปัจจุบัน
        double t = this.turnCount; // จำนวนเทิร์นปัจจุบัน

        // interest rate = b * log10(m) * ln(t)
        double r = b * Math.log10(m) * Math.log(t);

        // interest = m * (r / 100)
        double interest = m * (r / 100.0);

        this.budget += interest;

        this.budget = Math.min(this.budget, config.max_budget());

        System.out.println(this.name + " earned interest: " + (int)(interest) + ", new budget: " + (int)(this.budget));
    }

//    // คำนวณดอกเบี้ย current*interest/100
//    public void calculateInterest() {
//        double interest = this.budget * (config.interest_pct() / 100.0);
//        this.budget += interest;
//        this.budget = Math.min(this.budget, config.max_budget()); // ไม่ให้เกินงบประมาณสูงสุด
//    }

    public void incrementTurnCount() {
        this.turnCount++;
    }

    // Getter สำหรับ budget
    public double getBudget() {
        return budget;
    }

    public int getIntBudget() {
        return (int)(this.budget);
    }

    // Getter สำหรับ turnCount
    public int getTurnCount() {
        return turnCount;
    }

    public double setBudget(double budget) {
        return this.budget = budget;
    }

    //dummy done()
    public void done() {
        System.out.println(this.name + " has finished!");
    }

    //eiei
}
