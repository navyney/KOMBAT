package backend.expression;

import backend.entity.Minion;
import backend.parser.Expr;

public record LongLit(long val) implements Expr {
    public long eval(
            Minion minion) {
        return val;
    }

    public void prettyPrint(
            StringBuilder s) {
        s.append(val);
    }
}
