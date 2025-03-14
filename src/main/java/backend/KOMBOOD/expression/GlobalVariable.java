package backend.KOMBOOD.expression;

import backend.KOMBOOD.entity.Minion;
import backend.KOMBOOD.error.EvalError;
import backend.KOMBOOD.parser.Expr;

public record GlobalVariable(String name) implements Expr {
    @Override
    public long eval(Minion minion) throws EvalError {
        return minion.getValueGlobalVariable(name);
    }

    @Override
    public void prettyPrint(StringBuilder s) {
        s.append(name);
    }
}
