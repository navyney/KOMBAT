public class MinionType {
    private String name;
    private int defense;
    private Statement strategy; // กลยุทธ์ของมินเนี่ยน

//    public MinionType(String name, int defense, Statement strategy) {
//        this.name = name;
//        this.defense = defense;
//        this.strategy = strategy;
//    }

    public MinionType(String name, int defense) {
        this.name = name;
        this.defense = defense;
    }

    public String getName() {
        return name;
    }
    public int getDefense() {
        return defense;
    }
    public Statement getStrategy() {
        return strategy;
    }
}
