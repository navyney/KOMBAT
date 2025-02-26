package backend.KOMBOOD.expression;

import backend.KOMBOOD.entity.Minion;
import backend.KOMBOOD.error.EvalError;
import backend.KOMBOOD.parser.Expr;
import backend.KOMBOOD.utils.Direction;

public class NearbyExpr implements Expr {
    Direction direction;

    public NearbyExpr(Direction direction) {
        this.direction = direction;
    }

    @Override
    public long eval(Minion minion) throws EvalError {
        int row = minion.getRow();
        int col = minion.getCol();
        int def = minion.getType().getDefense();
        int hp = minion.getHp();
        int direc = direction.ordinal() + 1;
        if(direc == 1 && minion.onMap(row-1,col)){
            return hp*100+def*10+1;
        }
        else if(direc == 2 && minion.onMap(row-1,col+1)){
            return hp*100+def*10+2;
        }
        else if(direc == 3 && minion.onMap(row+1,col+1)){
            return hp*100+def*10+3;
        }
        else if(direc == 4 && minion.onMap(row+1,col)){
            return hp*100+def*10+4;
        }
        else if(direc == 5 && minion.onMap(row+1,col-1)){
            return hp*100+def*10+5;
        }
        else if(direc == 6 && minion.onMap(row-1,col-1)){
            return hp*100+def*10+6;
        }
        else{
            return 0;
        }
    }

    @Override
    public void prettyPrint(StringBuilder s) {

    }
}
