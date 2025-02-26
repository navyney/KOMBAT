package backend.KOMBOOD.expression;

import backend.KOMBOOD.entity.Minion;
import backend.KOMBOOD.parser.Expr;

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
