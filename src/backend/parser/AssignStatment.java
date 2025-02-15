package backend.parser;

import backend.entity.Minion;
import backend.error.EvalError;
import backend.error.EvalResultStatus;

public class AssignStatment extends Statement{
    Expr expression;
    String identifier;
    Long value;

    public AssignStatment(Expr expression, String identifier) {
        this.expression = expression;
        this.identifier = identifier;
    }
    @Override
    public EvalResultStatus evaluator(Minion minion) throws EvalError {
        value = expression.eval(minion);
        minion.assign(identifier, value);
        return EvalResultStatus.EXECUTE;
    }
}
