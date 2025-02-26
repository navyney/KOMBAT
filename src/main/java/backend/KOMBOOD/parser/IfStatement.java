package backend.KOMBOOD.parser;

import backend.KOMBOOD.entity.Minion;
import backend.KOMBOOD.error.EvalError;
import backend.KOMBOOD.error.EvalResultStatus;

public class IfStatement extends Statement {
    Expr expr;
    Statement trueStatement,falseStatement;
    Long value;

    public IfStatement(Expr expr, Statement trueStatement, Statement falseStatement) {
        this.expr = expr;
        this.trueStatement = trueStatement;
        this.falseStatement = falseStatement;
    }
    @Override
    public EvalResultStatus evaluator(Minion minion) throws EvalError {
        value = expr.eval(minion);
        if (value > 0) {
            return trueStatement.evaluator(minion);
        } else {
            return falseStatement.evaluator(minion);
        }
    }
}
