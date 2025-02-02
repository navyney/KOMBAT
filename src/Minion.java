import java.util.HashMap;

class Minion {
    private String name;
    private MinionType type;
    private int hp;
    private int row, col;
    private Player owner;
    private HashMap<String,Long> hmIdentifier = new HashMap<>();
    private Map map;

    public Minion(String name, MinionType type, int hp, Player owner, Map map) {
        this.name = name;
        this.type = type;
        this.hp = hp;
        this.owner = owner;
        this.map = map;
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

    public void assign(String identifier,long val){
        hmIdentifier.put(identifier,val);
    }

    public long getValueIdentifier(String identifier){
        return hmIdentifier.get(identifier);
    }

    public boolean spawn(int r, int c) {
        this.row = r-1;
        this.col = c-1;
        if (!map.isWall(this.row, this.col) && !map.isMinionHere(this.row, this.col)) {
            map.placeMinion(this.row, this.col, this);
            return true;
        }
        return false;
    }

    public void move(int direction) {
        int newRow = this.row;
        int newCol = this.col;

        if( direction==1 ){ //up
            newRow -= 1;
        } else if(direction == 2 ) { //upright
            newCol += 1;
        } else if(direction ==3 ) { //downright
            newCol += 1;
            newRow += 1;
        } else if(direction==4) { //down
            newRow += 1;
        } else if(direction == 5 ) { //downleft
            newRow -= 1;
        } else if(direction == 6 ) { //upleft
            newRow -= 1;
            newCol -= 1;
        } else {
            throw new IllegalArgumentException("Invalid direction");
        }

        if (!map.isWall(newRow, newCol) && !map.isMinionHere(newRow, newCol)) {
            map.removeMinion(this.row, this.col);

            this.row = newRow;
            this.col = newCol;

            map.placeMinion(this.row, this.col, this);
        } else {
            System.out.println("Can't move to (" + (newRow + 1) + "," + (newCol + 1) + ")!");
        }

    }

    public void takeDamage(long damage) {
        if (type.getDefense() > 0) {
            long reducedDamage = Math.max(0, damage - type.getDefense());
            this.hp -= reducedDamage;
            System.out.println(this.owner.getName() + " Minion HP: " + this.stringGetHp());
        } else {
            this.hp -= damage;
            System.out.println(this.owner.getName() + " Minion HP: " + this.stringGetHp());
        }
        if (hp <= 0) {
            System.out.println(name + " has been destroyed!");
        }
    }

    public void shoot(int direction, long damage) {
        int targetRow = this.row;
        int targetCol = this.col;

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
            throw new IllegalArgumentException("Invalid Area");
        }

        Minion target = map.getMinionAt(targetRow, targetCol);
        if (target != null) {
            System.out.println(name + " shoots at " + target.name);
            target.takeDamage(damage);

            if (target.getHp() <= 0) {
                map.removeMinion(targetRow, targetCol);
            }
        } else {
            System.out.println("No target to shoot at (" + (targetRow + 1) + "," + (targetCol + 1) + ")");
        }
    }


    private int getHp() {
        return hp;
    }

    private String stringGetHp() {
        return String.valueOf(hp);
    }

}
