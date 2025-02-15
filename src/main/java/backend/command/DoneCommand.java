package backend.command;

import backend.entity.Minion;
import backend.error.EvalError;
import backend.error.EvalResultStatus;
import backend.parser.Statement;

public class DoneCommand extends Statement {

    public DoneCommand() {
    }
    @Override
    public EvalResultStatus evaluator(Minion minion) throws EvalError {
            System.out.println("done");
            return EvalResultStatus.DONE;
    }
}
