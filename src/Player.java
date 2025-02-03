import java.util.ArrayList;

public class Player {
    //    String name;
    //    String type;
    private double maxBudget;
    private double budget;
    private ArrayList<Minion> minion;
    private ArrayList<Hex> area;

    public Player(double maxBudget, double budget) {
        //        this.type = type;
        //        this.name = name;
        this.maxBudget = maxBudget;
        this.budget = budget;
        this.minion = new ArrayList<Minion>();
        this.area = new ArrayList<Hex>();
    }
    public int getNumofMinion() {
        return minion.size();
    }

    public int getNumofArea() {
        return area.size();
    }

    public double getMaxBudget() {
        return maxBudget;
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

    public void spawnMinion(int r, int c) {
        // อย่าลืมเช็คเงินก่อน spawn และหักเงินหลัง spawn
        Minion m = new Minion(r,c);
        minion.add(m);
    }

    public void buyArea(int r, int c) {
        // อย่าลืมเช็คเงินก่อน buy และหักเงินหลัง buy
        Hex h = new Hex(r,c,false,1);
        area.add(h);
    }

    public void done(){
        System.out.println("Player has ended their turn.");
    }
    //eiei
}
