public class HexHex implements Hex {
    private int row;
    private int col;
    private boolean haveminion;
    private int own;

    public HexHex(int row, int col, boolean haveminion, int own) {
        this.row = row;
        this.col = col;
        this.haveminion = haveminion;
        this.own = own;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean isMinionHere() {
        return haveminion;
    }

    @Override
    public int owner() {
        /*
        if() { return 1;
        }else if() return -1;
         */
        return own;
    }
}