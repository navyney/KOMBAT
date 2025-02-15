package backend.strategy;

import backend.entity.Minion;
import backend.error.EvalError;
import backend.error.EvalResultStatus;

public interface iStrategy {
     EvalResultStatus evaluator(Minion minion) throws EvalError;
}
