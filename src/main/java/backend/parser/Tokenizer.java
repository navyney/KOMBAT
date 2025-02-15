package backend.parser;

import backend.error.LexicalError;

public interface Tokenizer {
    boolean hasNextToken();
    String peek();
    String consume() throws LexicalError;
}
