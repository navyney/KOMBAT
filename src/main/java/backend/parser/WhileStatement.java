package backend.parser;

import backend.entity.Minion;
import backend.error.EvalError;
import backend.error.EvalResultStatus;

public class WhileStatement extends Statement {
    Expr expr;
    Statement statement;
    Long value;
    public WhileStatement(Expr expr, Statement statement) {
        this.expr = expr;
        this.statement = statement;
    }
    @Override
    public EvalResultStatus evaluator(Minion minion) throws EvalError {
        value = expr.eval(minion);
        for (int counter = 0; counter < 10000 && value > 0; counter++){
            EvalResultStatus res = statement.evaluator(minion);
            if(res!=EvalResultStatus.EXECUTE) {
                return res;
            }
        }
        return EvalResultStatus.EXECUTE;
    }
}
