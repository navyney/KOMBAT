package backend.parser;

import backend.entity.Minion;
import backend.error.EvalError;
import backend.error.EvalResultStatus;
import backend.strategy.iStrategy;

public class Statement implements iStrategy {

    @Override
    public EvalResultStatus evaluator(Minion minion) throws EvalError {
        return EvalResultStatus.EXECUTE;
    }
}
