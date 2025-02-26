package backend.KOMBOOD.strategy;

import backend.KOMBOOD.entity.Minion;
import backend.KOMBOOD.error.EvalError;
import backend.KOMBOOD.error.EvalResultStatus;

public interface iStrategy {
     EvalResultStatus evaluator(Minion minion) throws EvalError;
}
