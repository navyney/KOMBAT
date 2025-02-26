package backend.KOMBOOD.command;

import backend.KOMBOOD.entity.Minion;
import backend.KOMBOOD.error.EvalError;
import backend.KOMBOOD.error.EvalResultStatus;
import backend.KOMBOOD.parser.Expr;
import backend.KOMBOOD.parser.Statement;
import backend.KOMBOOD.utils.Direction;

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
