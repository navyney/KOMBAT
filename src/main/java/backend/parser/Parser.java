package backend.parser;

import backend.error.EvalError;
import backend.error.LexicalError;
import backend.error.SyntaxError;
import backend.strategy.Strategy;

public interface Parser {
    Strategy parse() throws SyntaxError, LexicalError, EvalError;
}
