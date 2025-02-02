import java.util.List;

public class BlockStatement extends Statement {
    List<Statement> statements;

    public BlockStatement(List<Statement> statements) {
     this.statements = statements;
    }

    @Override
    public EvalResultStatus evaluator(Minion minion) throws EvalError {
        for (Statement statement : statements) {
            EvalResultStatus res = statement.evaluator(minion);
            if(res!=EvalResultStatus.EXECUTE) {
                return res;
            }
        }
        return EvalResultStatus.EXECUTE;
    }
}
