package backend.expression;

import backend.entity.Minion;
import backend.error.EvalError;
import backend.parser.Expr;

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
