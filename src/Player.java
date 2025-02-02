import java.util.ArrayList;

public class Player {
    String name;
    //    String type;
    private double maxBudget;
    private double budget;
    private ArrayList<Minion> minion;
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

    public ArrayList<Minion> getMinion() {
        return minion;
    }

    public ArrayList<Hex> getArea() {
        return area;
    }

    public void buyArea(int r, int c) {
        // อย่าลืมเช็คเงินก่อน buy และหักเงินหลัง buy
        Hex h = new HexHex(r,c,false,1);
        area.add(h);
    }

    public void done(){
        System.out.println("Player has ended their turn.");
    }
    //eiei
}
