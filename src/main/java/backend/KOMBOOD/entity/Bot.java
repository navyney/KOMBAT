package backend.KOMBOOD.entity;

import backend.KOMBOOD.map.Hex;
import backend.KOMBOOD.map.HexHex;
import backend.KOMBOOD.game.Main;
import backend.KOMBOOD.map.MapMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Bot extends Player {
    private Random random = new Random();

    public Bot(String name, String Id) {
        super(name, Id);
    }

    public void takeTurn(MapMap map) throws IOException {
        ArrayList<Minion> minions = getAllMinions();
        System.out.println("Bot " + getName() + " is taking a turn...");
        int r = random.nextInt(1000);
        if (r  % 2 == 0) {
            buyRandomArea(map);
        }else if (r % 3 == 0) {
            spawnRandomMinion(minions);
        }else{
            buyRandomArea(map);
            spawnRandomMinion(minions);
        }
        System.out.println("Bot " + getName() + " has finished its turn.");
    }

    private void buyRandomArea(MapMap map) {
        ArrayList<Hex> availableAreas = new ArrayList<>();

        for (int r = 0; r < map.getRows(); r++) {
            for (int c = 0; c < map.getCols(); c++) {
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

//    private void buyRandomMinion(ArrayList<Minion> minions) {
//        if (getBudget() < Main.getConfig().buy_minion_cost()) {
//            return;
//        }
//
//        MinionType[] types = MinionType.getAllMinionTypes();
//
//        if (types.length == 0) {
//            return;
//        }
//
//        MinionType randomType = types[random.nextInt(types.length)];
//        Minion minion = minions.get(random.nextInt(minions.size()));
//        buyMinion(randomType, minion);
//    }

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