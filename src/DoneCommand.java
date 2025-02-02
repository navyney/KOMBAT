public class DoneCommand extends Statement {

    public DoneCommand() {
    }
    @Override
    public EvalResultStatus evaluator(Minion minion) throws EvalError {
            System.out.println("done");
            return EvalResultStatus.DONE;
    }
}
