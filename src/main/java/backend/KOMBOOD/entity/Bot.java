package backend.KOMBOOD.entity;

import backend.KOMBOOD.config.ConfigFile;
import backend.KOMBOOD.game.GameState;
import backend.KOMBOOD.game.GameStatePVE;
import backend.KOMBOOD.map.Hex;
import backend.KOMBOOD.map.HexHex;
import backend.KOMBOOD.game.Main;
import backend.KOMBOOD.map.MapMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Bot extends Player {
    private Random random = new Random();
    private MapMap map = GameStatePVE.gamemap;
    private ArrayList<Minion> minions = getAllMinions();

    public Bot(String name) {
        super(name);
    }

    public void takeTurn() {
        System.out.println("Bot " + getName() + " is taking a turn...");

        if (random.nextBoolean() && canBuyArea()) {
            buyRandomArea();
        }

        if (random.nextBoolean() && canBuyMinion()) {
            buyRandomMinion();
        }

        if (random.nextBoolean() && canSpawnMinion()) {
            spawnRandomMinion();
        }

        System.out.println("Bot " + getName() + " has finished its turn.");
    }

    private void buyRandomArea() {
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

    private void buyRandomMinion() {
        if (getBudget() < Main.getConfig().buy_minion_cost()) {
            return;
        }

        MinionType[] types = MinionType.getAllMinionTypes();

        if (types.length == 0) {
            return;
        }

        MinionType randomType = types[random.nextInt(types.length)];
        Minion minion = minions.get(random.nextInt(minions.size()));
        buyMinion(randomType, minion);
    }

    private void spawnRandomMinion() {
        if (getMinion().isEmpty() || getBudget() < Main.getConfig().spawn_cost()) {
            return;
        }

        ArrayList<Minion> availableMinions = new ArrayList<>(getMinion());
        Minion minion = availableMinions.get(random.nextInt(availableMinions.size()));

        ArrayList<Hex> ownedAreas = getArea();
        if (ownedAreas.isEmpty()) {
            return;
        }

        Hex selectedHex = ownedAreas.get(random.nextInt(ownedAreas.size()));
        try {
            spawnMinion(minion, selectedHex.getRow(), selectedHex.getCol());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}