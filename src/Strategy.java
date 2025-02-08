import java.util.List;

public class Strategy implements iStrategy{
    List<Statement> statements;

    public Strategy(List<Statement> statements) {
        this.statements = statements;
    }
    @Override
    public EvalResultStatus evaluator(Minion minion) throws EvalError {
        for (Statement statement : statements) {
            EvalResultStatus res = statement.evaluator(minion);
            if(res!=EvalResultStatus.EXECUTE) {
                //System.out.println("exit");
                break;
            }
        }
        return EvalResultStatus.DONE;
    }

//    @Override
//    public EvalResultStatus evaluator(Minion minion) throws EvalError {
//        // ตรวจสอบว่า minion ถูก spawn แล้วหรือไม่
//        if (!minion.isSpawned()) {
//            System.out.println("Minion " + minion.getName() + " has not been spawned yet!");
//            return EvalResultStatus.NOT_EXECUTE; // do nothing if ยังไม่ได้ spawn
//        }
//
//        for (Statement statement : statements) {
//            EvalResultStatus res = statement.evaluator(minion);
//            if (res != EvalResultStatus.EXECUTE) {
//                break;
//            }
//        }
//        return EvalResultStatus.DONE;
//    }
}
