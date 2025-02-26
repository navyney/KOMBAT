package backend.KOMBOOD.command;

import backend.KOMBOOD.entity.Minion;
import backend.KOMBOOD.error.EvalError;
import backend.KOMBOOD.error.EvalResultStatus;
import backend.KOMBOOD.parser.Statement;

public class DoneCommand extends Statement {

    public DoneCommand() {
    }
    @Override
    public EvalResultStatus evaluator(Minion minion) throws EvalError {
            System.out.println("done");
            return EvalResultStatus.DONE;
    }
}
