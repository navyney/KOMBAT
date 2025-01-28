import java.util.Map;

public record BinaryArithExpr(
        Expr left, String op, Expr right)
            implements Expr {
        public long eval(
                Map<String, Long> bindings) throws EvalError {
            long lv = left.eval(bindings);
            long rv = right.eval(bindings);
            if (op.equals("+")) return lv + rv;
            if (op.equals("-")) return lv - rv;
            if (op.equals("*")) return lv * rv;
            if (op.equals("/")) return lv / rv;
            if (op.equals("%")) return lv % rv;
            if (op.equals("^")) return lv ^ rv;
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
