package backend.KOMBOOD.expression;

import backend.KOMBOOD.entity.Minion;
import backend.KOMBOOD.error.EvalError;
import backend.KOMBOOD.parser.Expr;

public record BinaryArithExpr (
        Expr left, String op, Expr right)
            implements Expr {
        public long eval(
                Minion minion) throws EvalError {
            long lv = left.eval(minion);
            long rv = right.eval(minion);
            if (op.equals("+")) return lv + rv;
            if (op.equals("-")) return lv - rv;
            if (op.equals("*")) return lv * rv;
            if (op.equals("/")) return lv / rv;
            if (op.equals("%")) return lv % rv;
            if (op.equals("^")) return (long)Math.pow(lv,rv);
            throw new EvalError("unknown op: " + op);
        }

        public void prettyPrint(StringBuilder s) {
            s.append("(");
            left.prettyPrint(s);
            s.append(op);
            right.prettyPrint(s);
            s.append(")");
        }
    }
