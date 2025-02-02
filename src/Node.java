interface Node {
        void prettyPrint(StringBuilder s);
    }
    interface Expr extends Node {
        long eval(Minion minion) throws EvalError;
    }
