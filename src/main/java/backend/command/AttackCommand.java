package backend.command;

import backend.entity.Minion;
import backend.error.EvalError;
import backend.error.EvalResultStatus;
import backend.parser.Expr;
import backend.parser.Statement;
import backend.utils.Direction;

public class AttackCommand extends Statement {
    Direction direction;
    Expr expression;

    public AttackCommand(Direction direction, Expr expression) {
        this.direction = direction;
        this.expression = expression;
    }

    @Override
    public EvalResultStatus evaluator(Minion minion) throws EvalError {

        long damage = expression.eval(minion);
        minion.shoot(direction.ordinal() + 1,damage);//ordinal is position ของ Enum
        return EvalResultStatus.SHOOT;
    }
}
