record LongLit(long val) implements Expr {
    public long eval(
            Minion minion) {
        return val;
    }

    public void prettyPrint(
            StringBuilder s) {
        s.append(val);
    }
}
