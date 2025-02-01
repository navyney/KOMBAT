class Minion {
    private String name;
    private MinionType type;
    private int row, col;
    private int hp;
    private int defense;

    public Minion(String name, MinionType type, int hp) {
        this.name = name;
        this.type = type;
        this.hp = hp;
        this.defense = type.getDefense();
        this.row = row;
        this.col = col;
    }

    public boolean spawn(int row, int col, Map map) {
        if (!map.isWall(row, col) && !map.isMinionHere(row, col)) {
            this.row = row;
            this.col = col;
            return true;
        }
        return false;
    }

    public void move(String direction) {
        if ("up".equals(direction)) {
            this.row -= 1;
        } else if ("down".equals(direction)) {
            this.row += 1;
        } else if ("upleft".equals(direction)) {
            this.row -= 1;
            this.col -= 1;
        } else if ("downleft".equals(direction)) {
            this.col -= 1;
        } else if ("upright".equals(direction)) {
            this.col += 1;
        } else if ("downright".equals(direction)) {
            this.col += 1;
            this.row += 1;
        }
    }

    public void shoot(Minion target) {
        int damage = 10; // ค่าความเสียหายพื้นฐาน
        target.takeDamage(damage);
        System.out.println(this.name + " shot " + target.name + " for " + damage + " damage!");
    }

    public void takeDamage(int damage) {
        int reducedDamage = Math.max(0, damage - this.defense);
        this.hp -= reducedDamage;
        System.out.println(this.name + " took " + reducedDamage + " damage! HP left: " + this.hp);
    }

    public int getHp() {
        return hp;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
