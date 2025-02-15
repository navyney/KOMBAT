package backend.parser;

import backend.error.LexicalError;
import backend.error.SyntaxError;

import java.util.NoSuchElementException;

import static java.lang.Character.*;

public class ExprTokenizer implements Tokenizer {
    private String src, next;  private int pos;
    public ExprTokenizer(String src) throws LexicalError {
        this.src = src;  pos = 0;
        computeNext();
    }
    @Override
    public boolean hasNextToken()
    { return next != null; }
    public void checkNextToken() {
        if (!hasNextToken()) throw new
                NoSuchElementException("no more tokens");
    }
    @Override
    public String peek() {
        checkNextToken();
        return next;
    }

    public boolean peek(String s) {
        if (!hasNextToken()) return false;
        return peek().equals(s);
    }
    @Override
    public String consume() throws LexicalError {
        checkNextToken();
        String result = next;
        computeNext();
        return result;
    }
    public void consume(String s)
            throws SyntaxError, LexicalError {
        if (peek(s))
            consume();
        else
            throw new SyntaxError(s + " expected, but found " + peek() + "\n" + leftString());
    }

    public String leftString(){
        return src.substring(pos);
    }

    private void computeNext() throws LexicalError {
        StringBuilder s = new StringBuilder();
        while (pos < src.length() && isSpace(src.charAt(pos)))
            pos++;
        if (pos == src.length())
        { next = null;  return; }
        char c = src.charAt(pos);
        if (isDigit(c)) {
            s.append(c);
            for (pos++; pos < src.length() &&
                    isDigit(src.charAt(pos)); pos++)
                s.append(src.charAt(pos));
        }
        else if (isLetter(c)) {
            s.append(c);
            for (pos++; pos < src.length() &&
                    isLetter(src.charAt(pos)); pos++)
                s.append(src.charAt(pos));

        }
        else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^'|| c == '(' || c == ')' || c == '=' || c == '{' || c == '}') {
            s.append(c);  pos++;
        }
        else throw new LexicalError("unknown character: " + c);
        next = s.toString();
    }
}
