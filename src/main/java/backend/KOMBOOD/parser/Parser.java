package backend.KOMBOOD.parser;

import backend.KOMBOOD.error.EvalError;
import backend.KOMBOOD.error.LexicalError;
import backend.KOMBOOD.error.SyntaxError;
import backend.KOMBOOD.strategy.Strategy;

public interface Parser {
    Strategy parse() throws SyntaxError, LexicalError, EvalError;
}
