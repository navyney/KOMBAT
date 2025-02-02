public class Statement implements iStrategy{

    @Override
    public EvalResultStatus evaluator(Minion minion) throws EvalError {
        return EvalResultStatus.EXECUTE;
    }
}
