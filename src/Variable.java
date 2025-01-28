import java.util.Map;

public record Variable(String name)
            implements Expr {
        public long eval(
                Map<String, Long> bindings) throws EvalError {
            if (bindings.containsKey(name))
                return bindings.get(name);
            throw new EvalError(
                    "undefined variable: " + name);
        }
        public void prettyPrint(
                StringBuilder s) {
            s.append(name);
        }
    }
