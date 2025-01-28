public interface Parser {
    Statement parse() throws SyntaxError, LexicalError, EvalError;
}
