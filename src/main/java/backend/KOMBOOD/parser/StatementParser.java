package backend.KOMBOOD.parser;

import backend.KOMBOOD.command.AttackCommand;
import backend.KOMBOOD.command.DoneCommand;
import backend.KOMBOOD.command.MoveCommand;
import backend.KOMBOOD.error.EvalError;
import backend.KOMBOOD.error.LexicalError;
import backend.KOMBOOD.error.SyntaxError;
import backend.KOMBOOD.expression.*;
import backend.KOMBOOD.strategy.Strategy;
import backend.KOMBOOD.utils.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static java.lang.Character.isDigit;

public class StatementParser implements Parser {
    private ExprTokenizer tkz;
    HashSet<String> hsIdentifiers = new HashSet<>(Arrays.asList(
            "ally", "done", "down", "downleft", "downright",
            "else", "if", "move", "nearby", "opponent",
            "shoot", "then", "up", "upleft", "upright", "while"));
    HashSet<String> specialVariable = new HashSet<>(Arrays.asList(
            "row", "col", "budget", "int", "maxbudget", "spawnsleft", "random"
    ));
    public StatementParser(ExprTokenizer tkz) {
        this.tkz = tkz;
    }

    @Override
    public Strategy parse() throws SyntaxError, LexicalError, EvalError {
        Strategy result = parseStrategy();

        // reject if there is remaining token
        if (tkz.hasNextToken())
            throw new SyntaxError("leftover token" + tkz.leftString());
        return result;
    }

    private Strategy parseStrategy() throws SyntaxError, LexicalError, EvalError {
        Statement s = parseStatement();
        List<Statement> ls = new ArrayList<>();
        ls.add(s);
        while (tkz.hasNextToken()) {
            Statement ws = parseStatement();
            ls.add(ws);
        }
        Strategy strategy = new Strategy(ls);
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
            String identifier = tkz.consume();
            tkz.consume("=");
            Expr val = parseExpression();
            AssignStatment a = new AssignStatment(val, identifier);
            return a;
        }
        else{
            throw new SyntaxError("cannot be used as identify");
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
        else{//done
            Statement s = new DoneCommand();
            tkz.consume();
            return s;
        }
    }

    private MoveCommand parseMoveCommand() throws SyntaxError, LexicalError {
        tkz.consume("move");
        MoveCommand m = new MoveCommand(parseDirection());
        return m;
    }

    private Statement parseAttackCommand() throws SyntaxError, LexicalError {
        tkz.consume("shoot");
        Direction d = parseDirection();
        Expr val = parseExpression();
        AttackCommand a = new AttackCommand(d,val);
        return a;
    }

    private Direction parseDirection() throws SyntaxError, LexicalError {
        switch (tkz.consume()) {
            case "up": return Direction.UP;
            case "upright": return Direction.UPRIGHT;
            case "downright": return Direction.DOWNRIGHT;
            case "down": return Direction.DOWN;
            case "downleft": return Direction.DOWNLEFT;
            case "upleft": return Direction.UPLEFT;
        };
        throw new SyntaxError("invalid direction");
    }

    private Statement parseBlockStatement() throws SyntaxError, LexicalError, EvalError {
        tkz.consume("{");
        List<Statement> ls = new ArrayList<>();
        while (!tkz.peek("}")) {
            Statement ws = parseStatement();
            ls.add(ws);
        }
        tkz.consume("}");
        Statement s = new BlockStatement(ls);
        return s;
    }

    private Statement parseIfStatement() throws SyntaxError, LexicalError, EvalError {
        tkz.consume("if");
        tkz.consume("(");
        Expr val = parseExpression();
        tkz.consume(")");
        tkz.consume("then");
        Statement trueStatement = parseStatement();
        tkz.consume("else");
        Statement falseStatement = parseStatement();
        Statement s = new IfStatement(val, trueStatement, falseStatement);
        return s;

    }

    private Statement parseWhileStatement() throws SyntaxError, LexicalError, EvalError {
        tkz.consume("while");
        tkz.consume("(");
        Expr val = parseExpression();
        tkz.consume(")");
        Statement StatementInWhile = parseStatement();
        Statement s = new WhileStatement(val, StatementInWhile);
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
                if(!specialVariable.contains(tkz.peek())) {
                    return new SpecialVariable(tkz.consume());
                }
                else if(Character.isUpperCase(tkz.peek().charAt(0))){
                    return new GlobalVariable(tkz.consume());
                }
                else{
                    return new Variable(tkz.consume());
                }
            }
        }
    }

    private Expr parseInfoExpression() throws SyntaxError, LexicalError {
        if(tkz.peek("ally")){
            tkz.consume("ally");
            Expr ally = new AllyExpr();
            return ally;
        }
        else if(tkz.peek("opponent")){
            tkz.consume("opponent");
            Expr opponent = new OpponentExpr();
            return opponent;
        }
        else{
            tkz.consume("nearby");
            Direction d = parseDirection();
            Expr nearby = new NearbyExpr(d);
            return nearby;
        }
    }
}
