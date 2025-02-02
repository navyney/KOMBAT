public class MoveCommand extends Statement{
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
