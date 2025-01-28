import java.util.Map;

record LongLit(long val) implements Expr {
    public long eval(
            Map<String, Long> bindings) {
        return val;
    }

    public void prettyPrint(
            StringBuilder s) {
        s.append(val);
    }
}
