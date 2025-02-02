import java.util.ArrayList;

public class Player {
    String name;
    //    String type;
    private double maxBudget;
    private double budget;
    private ArrayList<Minion> minion = new ArrayList<>();
    private ArrayList<Hex> area;

    public Player(String name, double budget) {
        //        this.type = type;
        this.name = name;
        this.maxBudget = maxBudget;
        this.budget = budget;
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

        if (hex.owner() == 0) { // ถ้าไม่มีเจ้าของ
            if (this.budget >= 1.0) { // ตรวจสอบเงิน สมมติHex ราคา 1
                hex.setOwner(this.name.equals("1") ? 1 : 2);
                this.area.add(hex);
                this.budget -= 1.0;
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

    public void done(){
        System.out.println("Player has ended their turn.");
    }
    //eiei
}
