package backend.KOMBOOD.command;

import backend.KOMBOOD.entity.Minion;
import backend.KOMBOOD.error.EvalError;
import backend.KOMBOOD.error.EvalResultStatus;
import backend.KOMBOOD.parser.Statement;
import backend.KOMBOOD.utils.Direction;

public class MoveCommand extends Statement {
    Direction direction;

    public MoveCommand(Direction direction) {
        this.direction = direction;
    }
    @Override
    public EvalResultStatus evaluator(Minion minion) throws EvalError {
        minion.move(direction.ordinal() + 1);//ordinal is position ของ Enum
        return EvalResultStatus.MOVE;
    }
}
