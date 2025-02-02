class Minion {
    private String name;
    private MinionType type;
    private int hp;
    private int row, col;
    private Player owner;

    public Minion(String name, MinionType type, int hp, Player owner) {
        this.name = name;
        this.type = type;
        this.hp = hp;
        this.owner = owner;
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


    public boolean spawn(int r, int c, Map map) {
        this.row = r-1;
        this.col = c-1;
        if (!map.isWall(this.row, this.col) && !map.isMinionHere(this.row, this.col)) {
            map.placeMinion(this.row, this.col, this);
            return true;
        }
        return false;
    }

    public void move(String direction, Map map) {
        int newRow = this.row;
        int newCol = this.col;

        if ("up".equals(direction)) {
            newRow -= 1;
        } else if ("down".equals(direction)) {
            newRow += 1;
        } else if ("upleft".equals(direction)) {
            newRow -= 1;
            newCol -= 1;
        } else if ("downleft".equals(direction)) {
            newCol -= 1;
        } else if ("upright".equals(direction)) {
            newCol += 1;
        } else if ("downright".equals(direction)) {
            newCol += 1;
            newRow += 1;
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


    public void takeDamage(int damage) {
        if (type.getDefense() > 0) {
            int reducedDamage = Math.max(0, damage - type.getDefense());
            this.hp -= reducedDamage;
        } else {
            this.hp -= damage;
        }
        if (hp <= 0) {
            System.out.println(name + " has been destroyed!");
        }
    }

    public void shoot(int targetRow, int targetCol, Map map, int damage) {
        int tr = targetRow-1;
        int tc = targetCol-1;
        Minion target = map.getMinionAt(tr, tc);
        if (target != null) {
            System.out.println(name + " shoots at " + target.name);
            target.takeDamage(damage);
            System.out.println(target.owner.getName() + " Minion HP: " + target.stringGetHp());
            if (target.getHp() <= 0) {
                map.removeMinion(tr, tc);
            }
        } else {
            System.out.println("No valid target at (" + tr + ", " + tc + ")");
        }
    }

    private int getHp() {
        return hp;
    }

    private String stringGetHp() {
        return String.valueOf(hp);
    }
}
