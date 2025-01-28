import java.util.Arrays;
import java.util.HashSet;

import static java.lang.Character.isDigit;

public class StatementParser implements Parser {
    private ExprTokenizer tkz;
    HashSet<String> hsIdentifiers = new HashSet<>(Arrays.asList(
            "ally", "done", "down", "downleft", "downright",
            "else", "if", "move", "nearby", "opponent",
            "shoot", "then", "up", "upleft", "upright", "while"));
    public StatementParser(ExprTokenizer tkz) {
        this.tkz = tkz;
    }

    @Override
    public Statement parse() throws SyntaxError, LexicalError, EvalError {
        Statement result = parseStrategy();

        // reject if there is remaining token
        if (tkz.hasNextToken())
            throw new SyntaxError("leftover token" + tkz.leftString());
        return result;
    }

    private Statement parseStrategy() throws SyntaxError, LexicalError, EvalError {
        Statement strategy = parseStatement();
        while (tkz.hasNextToken()) {
            parseStatement();
        }
        return strategy;
    }

    private Statement parseStatement() throws SyntaxError, LexicalError, EvalError {
        if(tkz.peek("while")){
            Statement s = parseWhileStatement();
            return s;
        }
        else if(tkz.peek("if")){
            Statement s = parseIfStatement();
            return s;
        }
        else if(tkz.peek("{")){
            Statement s = parseBlockStatement();
            return s;
        }
        else{
            Statement s = parseCommand();
            return s;
        }
    }

    private Statement parseCommand() throws LexicalError {
        if(tkz.peek("done") || tkz.peek("move") || tkz.peek("shoot")){
            Statement s = parseActionCommand();
            return s;
        }
        else{
            Statement s = parseAssignStatement();
            return s;
        }
    }

    private Statement parseAssignStatement() throws SyntaxError, LexicalError {
        if(!hsIdentifiers.contains(tkz.peek())) {
            tkz.consume();
            tkz.consume("=");
            Expr val = parseExpression();
            return null;
        }
        else{
            throw new SyntaxError("cannot be used as identify" + tkz.leftString());
        }
    }

    private Statement parseActionCommand() throws SyntaxError, LexicalError {
        if(tkz.peek("shoot")){
            Statement s = parseAttackCommand();
            return s;
        }
        else if(tkz.peek("move")){
            Statement s = parseMoveCommand();
            return s;
        }
        else{
            tkz.consume("done");
            return null;
        }
    }

    private Statement parseMoveCommand() throws SyntaxError, LexicalError {
        tkz.consume("move");
        Statement s = parseDirection();
        return s;
    }

    private Statement parseAttackCommand() throws SyntaxError, LexicalError {
        tkz.consume("shoot");
        Statement s = parseDirection();
        Expr val = parseExpression();
        return s;
    }

    private Statement parseDirection() throws SyntaxError, LexicalError {
        if(tkz.peek("up")){
            tkz.consume("up");
        }
        else if(tkz.peek("down")){
            tkz.consume("down");
        }
        else if(tkz.peek("upleft")){
            tkz.consume("upleft");
        }
        else if(tkz.peek("upright")){
            tkz.consume("upright");
        }
        else if(tkz.peek("downleft")){
            tkz.consume("downleft");
        }
        else if(tkz.peek("downright")){
            tkz.consume("downright");
        }
        else{
            throw new SyntaxError("invalid direction");
        }
        return null;
    }

    private Statement parseBlockStatement() throws SyntaxError, LexicalError, EvalError {
        tkz.consume("{");
        while(!tkz.peek("}")) {
            Statement s = parseStatement();
        }
        tkz.consume("}");
        return null;
    }

    private Statement parseIfStatement() throws SyntaxError, LexicalError, EvalError {
        tkz.consume("if");
        tkz.consume("(");
        Expr val = parseExpression();
        tkz.consume(")");
        tkz.consume("then");
            Statement s = parseStatement();
            tkz.consume("else");
            Statement s1 = parseStatement();

        return s;

    }

    private Statement parseWhileStatement() throws SyntaxError, LexicalError, EvalError {
        tkz.consume("while");
        tkz.consume("(");
        Expr val = parseExpression();
        tkz.consume(")");
        Statement s = parseStatement();
        return s;
    }

    private Expr parseExpression() throws SyntaxError, LexicalError {
        Expr val = parseTerm();
        while (tkz.peek("+") || tkz.peek("-")) {
            val = new BinaryArithExpr(val, tkz.consume(), parseTerm());
        }
        return val;
    }

    private Expr parseTerm() throws SyntaxError, LexicalError {
        Expr val = parseFactor();
        while (tkz.peek("*") || tkz.peek("/") || tkz.peek("%")) {
            val = new BinaryArithExpr(val, tkz.consume(), parseFactor());
        }
        return val;
    }

    private Expr parseFactor() throws SyntaxError, LexicalError {
        Expr val = parsePower();
        while (tkz.peek("^")) {
            val = new BinaryArithExpr(val, tkz.consume(), parsePower());
        }
        return val;
    }

    private Expr parsePower() throws SyntaxError, LexicalError {
        if (tkz.peek("(")) {
            tkz.consume("(");
            Expr val = parseExpression();
            tkz.consume(")");
            return val;
        }
        else if(tkz.peek("ally") || tkz.peek("opponent") || tkz.peek("nearby")) {
            Expr InfoExpr = parseInfoExpression();
            return InfoExpr;
        }
        else {
            if (isDigit(tkz.peek().charAt(0))){
                long token = Long.parseLong(tkz.consume());
                Expr val = new LongLit(token);
                return val;
            }
            else{
                Expr v = new Variable(tkz.consume());
                return v;
            }
        }
    }

    private Expr parseInfoExpression() throws SyntaxError, LexicalError {
        if(tkz.peek("ally")){
            tkz.consume("ally");
        }
        else if(tkz.peek("opponent")){
            tkz.consume("opponent");
        }
        else{
            tkz.consume("nearby");
            Statement s = parseDirection();
        }
        return null;
    }
}
