import java.util.Map;

    interface Node {
        void prettyPrint(StringBuilder s);
    }
    interface Expr extends Node {
        long eval(Map<String, Long> bindings) throws EvalError;
    }
