package backend.KOMBOOD.entity;

import backend.KOMBOOD.strategy.Strategy;

import java.util.HashMap;
import java.util.Map;

public class MinionType {
    private String name;
    private int defense;
    private Strategy strategy; // Strategy ของมินเนี่ยน
    private static Map<String, MinionType> minionTypes = new HashMap<>();

    public MinionType(String name, int defense, Strategy strategy) {
        this.name = name;
        this.defense = defense;
        this.strategy = strategy;
    }

//    public MinionTypeMap(String name, int defense) {
//        this(name, defense, null); // เรียก Constructor หลัก
//    }

    public static void addMinionType(String name, int defense, Strategy strategy) {
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
//            System.out.println("- " + name + " (DEF: " + type.getDefense() + ", Strategy: "
//                    + (type.getStrategy() != null ? type.getStrategy().toString() : "None") + ")");
            System.out.println("- " + name + " (DEF: " + type.getDefense() + ")");
        }
    }

    // Getter Methods
    public String getTypeName() {
        return name;
    }

    public int getDefense() {
        return defense;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public static MinionType[] getAllMinionTypes() {
        return minionTypes.values().toArray(new MinionType[0]);
    }
}
