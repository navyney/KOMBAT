package backend.KOMBOOD.expression;

import backend.KOMBOOD.entity.Minion;
import backend.KOMBOOD.error.EvalError;
import backend.KOMBOOD.parser.Expr;

public record Variable(String name)
            implements Expr {
        public long eval(Minion minion) throws EvalError {
            return minion.getValueIdentifier(name);
        }
        public void prettyPrint(
                StringBuilder s) {
            s.append(name);
        }
    }
