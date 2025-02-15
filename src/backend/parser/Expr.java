package backend.parser;

import backend.entity.Minion;
import backend.error.EvalError;

interface Node {
        void prettyPrint(StringBuilder s);
    }
    public interface Expr extends Node {
        long eval(Minion minion) throws EvalError;
    }
