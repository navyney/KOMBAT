public class Player {
    private String name;
    private double budget;
    private int numofMinion;
    private int numofArea;

    public Player(String name) {
        //double budget, int numofMinion, int numofArea
        //        this.type = type;
        this.name = name;
        this.budget = budget;
        this.numofMinion = 0;
        this.numofArea = 5;
    }

    public String getName() {
        return name;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public int getNumofMinion() {
        return numofMinion;
    }

    public void setNumofMinion(int numofMinion) {
        this.numofMinion = numofMinion;
    }

    public int getNumofArea() {
        return numofArea;
    }

    public void setNumofArea(int numofArea) {
        this.numofArea = numofArea;
    }
}
