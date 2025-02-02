public interface Parser {
    Strategy parse() throws SyntaxError, LexicalError, EvalError;
}
