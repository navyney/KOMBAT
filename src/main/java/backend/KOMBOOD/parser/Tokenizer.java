package backend.KOMBOOD.parser;

import backend.KOMBOOD.error.LexicalError;

public interface Tokenizer {
    boolean hasNextToken();
    String peek();
    String consume() throws LexicalError;
}
