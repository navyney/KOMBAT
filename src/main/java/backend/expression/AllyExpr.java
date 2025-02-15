package backend.expression;

import backend.entity.Minion;
import backend.error.EvalError;
import backend.parser.Expr;

public class AllyExpr implements Expr {

    public AllyExpr(){
    }

    @Override
    public long eval(Minion minion) throws EvalError {
        int row = minion.getRow();
        int col = minion.getCol();
        for(int dis = 1;dis < 8;dis++){
            if(minion.onMap(row-dis,col) && minion.getOwner().getMinion().contains(minion.getMap().getMinionAt(row-dis,col))){
                return dis*10+1;
            }
            else if(minion.onMap(row-dis,col+dis) && minion.getOwner().getMinion().contains(minion.getMap().getMinionAt(row-dis,col+dis))){
                return dis*10+2;
            }
            else if(minion.onMap(row+dis,col+dis) && minion.getOwner().getMinion().contains(minion.getMap().getMinionAt(row+dis,col+dis))){
                return dis*10+3;
            }
            else if(minion.onMap(row+dis,col) && minion.getOwner().getMinion().contains(minion.getMap().getMinionAt(row+dis,col))){
                return dis*10+4;
            }
            else if(minion.onMap(row+dis,col-dis) && minion.getOwner().getMinion().contains(minion.getMap().getMinionAt(row+dis,col-dis))){
                return dis*10+5;
            }
            else if(minion.onMap(row-dis,col-dis) && minion.getOwner().getMinion().contains(minion.getMap().getMinionAt(row-dis,col-dis))){
                return dis*10+6;
            }
            else{
            }
        }
        return 0;
    }

    @Override
    public void prettyPrint(StringBuilder s) {

    }
}
