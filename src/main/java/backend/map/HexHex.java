package backend.map;

public class HexHex implements Hex {
    private int row;
    private int col;
    private boolean haveminion;
    private int own; // 0 = ไม่มีเจ้าของ, 1 = Player1, 2 = Player2

    public HexHex(int row, int col, boolean haveminion, int own) {
        this.row = row;
        this.col = col;
        this.haveminion = haveminion;
        this.own = own;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getCol() {
        return col;
    }

    @Override
    public boolean isMinionHere() {
        return haveminion;
    }

    @Override
    public int owner() {
        return own;
    }

    @Override
    public void setOwner(int owner) {
        this.own = owner;
    }
}