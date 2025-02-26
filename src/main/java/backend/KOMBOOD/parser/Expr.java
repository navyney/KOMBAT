package backend.KOMBOOD.parser;

import backend.KOMBOOD.entity.Minion;
import backend.KOMBOOD.error.EvalError;

interface Node {
        void prettyPrint(StringBuilder s);
    }
    public interface Expr extends Node {
        long eval(Minion minion) throws EvalError;
    }
