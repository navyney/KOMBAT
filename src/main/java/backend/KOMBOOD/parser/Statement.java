package backend.KOMBOOD.parser;

import backend.KOMBOOD.entity.Minion;
import backend.KOMBOOD.error.EvalError;
import backend.KOMBOOD.error.EvalResultStatus;
import backend.KOMBOOD.strategy.iStrategy;

public class Statement implements iStrategy {

    @Override
    public EvalResultStatus evaluator(Minion minion) throws EvalError {
        return EvalResultStatus.EXECUTE;
    }
}
