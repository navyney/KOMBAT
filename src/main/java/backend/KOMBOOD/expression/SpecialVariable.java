package backend.KOMBOOD.expression;

import backend.KOMBOOD.entity.Minion;
import backend.KOMBOOD.error.EvalError;
import backend.KOMBOOD.parser.Expr;

public record SpecialVariable(String name) implements Expr{
    public long eval(Minion minion) throws EvalError {
        switch (name) {
            case "row":
                return minion.getRow();
            case "col":
                return minion.getCol();
            case "budget":
                return (long) minion.getOwner().getBudget();
            case "int":
                return minion.getOwner().getIntBudget();
            case "maxbudget":
                return (long) minion.getOwner().getMaxBudget();
            case "spawnsleft":
                return minion.getOwner().getSpawnRemaining();
            case "random":
                return (long) (Math.random() * 1000);
            default:
                throw new EvalError("Unknown variable: " + name);
        }
    }
    public void prettyPrint(
            StringBuilder s) {
        s.append(name);
    }
}
