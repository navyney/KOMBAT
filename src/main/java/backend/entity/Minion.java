package backend.entity;

import backend.config.ConfigFile;
import backend.map.HexHex;
import backend.map.MapMap;
import backend.game.Main;

import java.io.IOException;
import java.util.HashMap;

public class Minion {
    private String name;
    private MinionType type;
    private int hp;
    private int row = -1 ;
    private int col = -1 ;
    private Player owner;
    private HashMap<String,Long> hmIdentifier = new HashMap<>();
    private MapMap map;
    private ConfigFile config = Main.getConfig();
    private boolean actedThisTurn = false;

    public Minion(MinionType type, int hp, Player owner, MapMap map) {
        this.name = type.getTypeName();
        this.type = type;
        this.hp = hp;
        this.owner = owner;
        this.map = map;
    }

    //for debug
    public boolean isOwnedBy(Player player) {
        if (this.owner == player) {
            return true;
        } else {
            System.out.println("Hex at (" + getRow() + "," + getCol() + ") is NOT owned by Player " + player.getName());
            return false;
        }
    }

    public boolean isSpawned() {
        return row != -1 && col != -1;
    }

    public MinionType getType() {
        return type;
    }

    public int getRow() {
        return this.row+1;
    }

    public int getCol() {
        return this.col+1;
    }

    public Player getOwner() {
        return owner;
    }

    public boolean onMap(int r, int c) {
        return this.map.isMinionHere(r, c);
    }

    public void assign(String identifier,long val){
        hmIdentifier.put(identifier,val);
    }

    public int getHp() {
        return hp;
    }

    public String stringGetHp() {
        return String.valueOf(hp);
    }

    public String getName() {
        return this.name;
    }

    public MapMap getMap() {
        return this.map;
    }

    public long getValueIdentifier(String identifier){
        return hmIdentifier.get(identifier);
    }

    public boolean spawn(int r, int c) throws IOException {
        this.row = r - 1;
        this.col = c - 1;

        HexHex hex = (HexHex) map.getHexAt(r, c);

        if (hex == null) {
            System.out.println("Invalid area!");
            return false;
        }

        if (hex.owner() == (this.owner.getName().equals("Player1") ? 1 : 2)) {
            if (!map.isWall(this.row, this.col) && !map.isMinionHere(this.row, this.col)) {
                map.placeMinion(this.row, this.col, this);
                owner.addMinion(this);
                return true;
            } else {
                System.out.println("Cannot spawn minion here!");
                return false;
            }
        } else {
            System.out.println("You do not own this area!");
            return false;
        }
    }

    public void move(int direction) {
        if (!isSpawned()) {
            throw new IllegalArgumentException("Minion " + this.name + " has not been spawned yet!");
        }

        if (owner.getBudget() < config.move_cost()){
            System.out.println("Not enough budget to move!");
            return;
        }

        actedThisTurn = true;

        int newRow = this.row ;
        int newCol = this.col;

        if (direction == 1) { // up
            newRow -= 1;
        } else if (direction == 2) { // upright
            if ( this.col%2 == 1 ) {
                newCol += 1 ;
                newRow -= 1 ;
            } else {
                newCol += 1 ;
            }
        } else if (direction == 3) { // downright
            if ( this.col%2 == 1 ) {
                newCol += 1 ;
            } else {
                newCol += 1 ;
                newRow += 1 ;
            }
        } else if (direction == 4) { // down
            newRow += 1;
        } else if (direction == 5) { // downleft
            if ( this.col%2 == 1 ) {
                newCol -= 1 ;
            } else {
                newCol -= 1 ;
                newRow += 1 ;
            }
        } else if (direction == 6) { // upleft
            if ( newCol%2 == 1 ) {
                newCol -= 1 ;
                newRow -= 1 ;
            } else {
                newCol -= 1 ;
            }
        } else {
            throw new IllegalArgumentException("Invalid direction");
        }

        if (newRow < 0 || newRow >= map.getRows() || newCol < 0 || newCol >= map.getCols()) {
            System.out.println("I wonder why you want to go out of the map.");
            System.out.println("Now you are at (" + (this.row + 1) + "," + (this.col + 1) + ")");
            return ;
        }

        //HexHex hex = (HexHex) map.getHexAt(newRow + 1, newCol + 1);

        if (!map.isWall(newRow, newCol) && !map.isMinionHere(newRow, newCol)) {
            map.removeMinion(this.row, this.col);
            this.row = newRow;
            this.col = newCol;
            map.placeMinion(this.row, this.col, this);
            System.out.println("Moved to (" + (this.row + 1) + "," + (this.col + 1) + ")");
        } else {
            System.out.println("Cannot move to (" + (newRow + 1) + "," + (newCol + 1) + ")") ;
            System.out.println("Now you are at (" + (this.row + 1) + "," + (this.col + 1) + ")");
        }
    }

    public void takeDamage(long damage) {
        if (type.getDefense() > 0) {
            long reducedDamage = damage - type.getDefense();
            if (reducedDamage < 0) {
                this.hp += reducedDamage;
            } else {
                this.hp -= reducedDamage;
            }
            System.out.println(this.owner.getName() + " Minion HP: " + this.stringGetHp());
        } else {
            this.hp -= damage;
            System.out.println(this.owner.getName() + " Minion HP: " + this.stringGetHp());
        }
        if (hp <= 0) {
            owner.getMinion().remove(this);
            map.removeMinion(this.row, this.col);
            System.out.println(name + " has been destroyed!");
        }
    }

    public void shoot(int direction, long damage) {
        actedThisTurn = true;
        if (owner.getBudget() < damage) {
            System.out.println("Not enough budget to shoot!");
            return ;
        }

        int targetRow = this.row ;
        int targetCol = this.col ;

        if (direction == 1) { // up
            targetRow -= 1;
        } else if (direction == 2) { // upright
            targetRow -= 1;
            targetCol += 1;
        } else if (direction == 3) { // downright
            targetRow += 1;
            targetCol += 1;
        } else if (direction == 4) { // down
            targetRow += 1;
        } else if (direction == 5) { // downleft
            targetRow += 1;
            targetCol -= 1;
        } else if (direction == 6) { // upleft
            targetRow -= 1;
            targetCol -= 1;
        } else {
            throw new IllegalArgumentException("Invalid direction");
        }

        if (targetRow < 0 || targetRow >= map.getRows() || targetCol < 0 || targetCol >= map.getCols()) {
            owner.setBudget(owner.getBudget() - damage);
            System.out.println("There is not on the map at all LOL");
        }

        if (Math.abs(targetRow - this.row) > 1 || Math.abs(targetCol - this.col) > 1) {
            System.out.println("You can only shoot at adjacent hexes!");
            return;
        }

        Minion target = map.getMinionAt(targetRow, targetCol);
        if (target != null) {
            System.out.println(name + " shoots at " + target.name);
            target.takeDamage(damage);
            owner.setBudget(owner.getBudget() - damage);
        } else {
            owner.setBudget(owner.getBudget() - damage);
            System.out.println("Your action damage nothing");
        }
    }

    public boolean hasActedThisTurn() {
        return actedThisTurn ;
    }

    public void resetActionFlag() {
        actedThisTurn = false ;
    }
}
