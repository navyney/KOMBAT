import java.util.HashMap;
import java.util.Map;

public class MinionType {
    private String name;
    private int defense;
    private Statement strategy; // Strategy ของมินเนี่ยน
    private static Map<String, MinionType> minionTypes = new HashMap<>();

    public MinionType(String name, int defense, Statement strategy) {
        this.name = name;
        this.defense = defense;
        this.strategy = strategy;
    }

//    public MinionTypeMap(String name, int defense) {
//        this(name, defense, null); // เรียก Constructor หลัก
//    }

    public static void addMinionType(String name, int defense, Statement strategy) {
        MinionType type = new MinionType(name, defense, strategy);
        minionTypes.put(name, type);
    }

//    public static void addMinionType(String name, int defense) {
//        addMinionType(name, defense, null); // เรียกแบบไม่มี strategy
//    }

    public static MinionType getMinionType(String name) {
        return minionTypes.get(name);
    }

    public static void displayMinionTypes() {
        System.out.println("Available Minion Types:");
        for (String name : minionTypes.keySet()) {
            MinionType type = minionTypes.get(name);
            System.out.println("- " + name + " (DEF: " + type.getDefense() + ", Strategy: "
                    + (type.getStrategy() != null ? type.getStrategy().toString() : "None") + ")");
        }
    }

    // Getter Methods
    public String getTypeName() {
        return name;
    }

    public int getDefense() {
        return defense;
    }

    public Statement getStrategy() {
        return strategy;
    }
}
