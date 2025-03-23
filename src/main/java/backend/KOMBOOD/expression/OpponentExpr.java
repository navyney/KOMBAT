package backend.KOMBOOD.expression;

import backend.KOMBOOD.entity.Minion;
import backend.KOMBOOD.error.EvalError;
import backend.KOMBOOD.parser.Expr;

public class OpponentExpr implements Expr {

    public OpponentExpr(){
    }

    @Override
    public long eval(Minion minion) throws EvalError {
        int row = minion.getRow()-1;
        int col = minion.getCol()-1;
//        System.out.println(row + " " + col);

        for(int dis = 1;dis < 8;dis++){
            if(col%2 == 1) {


//                System.out.println("Roe - Dis: " + (row - dis) + " Col: " + col);
                //Minion x = minion.getMap().getMinionAt(row - dis, col);
                //if(x == null) System.out.println("Null");
                //else System.out.println(x.getOwner().getName());
                if(minion.onMap(row-dis,col) && !minion.getOwner().getMinion().contains(minion.getMap().getMinionAt(row - dis,col))){
                    System.out.println(1);
                    return dis*10 + 1;
                }
                else if (minion.onMap(row , col + dis) && !minion.getOwner().getMinion().contains(minion.getMap().getMinionAt(row , col + dis))) {
                    System.out.println(2);
                    return dis * 10 + 2;
                }
                else if (minion.onMap(row+dis, col + dis) && !minion.getOwner().getMinion().contains(minion.getMap().getMinionAt(row+dis, col + dis))) {
                    System.out.println(3);
                    return dis * 10 + 3;
                }
                else if(minion.onMap(row+dis,col) && !minion.getOwner().getMinion().contains(minion.getMap().getMinionAt(row+dis,col))){
                    System.out.println(4);
                    return dis*10+4;
                }
                else if(minion.onMap(row+dis,col-dis) && !minion.getOwner().getMinion().contains(minion.getMap().getMinionAt(row+dis,col-dis))){
                    System.out.println((row+dis) + " " + 5 + " " + (col-dis));
                    return dis*10+5;
                }
                else if(minion.onMap(row,col-dis) && !minion.getOwner().getMinion().contains(minion.getMap().getMinionAt(row,col-dis))){
                    System.out.println(6);
                    return dis*10+6;
                }
            }
            else if(col %2 == 0){
//                System.out.println("Roe - Dis: " + (row - dis) + " Col: " + col);
                //Minion x = minion.getMap().getMinionAt(row - dis, col);
                //if(x == null) System.out.println("Null");
                //else System.out.println(x.getOwner().getName());
                if(minion.onMap(row-dis,col) && !minion.getOwner().getMinion().contains(minion.getMap().getMinionAt(row-dis,col))){
                    System.out.println(1);
                    return dis*10+1;
                }
                else if (minion.onMap(row - dis, col + dis) && !minion.getOwner().getMinion().contains(minion.getMap().getMinionAt(row - dis, col + dis))) {
                    System.out.println(2);
                    return dis * 10 + 2;
                }
                else if (minion.onMap(row, col + dis) && !minion.getOwner().getMinion().contains(minion.getMap().getMinionAt(row, col + dis))) {
                    System.out.println(3);
                    return dis * 10 + 3;
                }
                else if(minion.onMap(row+dis,col) && !minion.getOwner().getMinion().contains(minion.getMap().getMinionAt(row+dis,col))){
                    System.out.println(4);
                    return dis*10+4;
                }
//                else{
//                    System.out.println("-----------------------------------------");
//                    System.out.println(minion.onMap(row+dis,col));
//                    System.out.println("----------------------------------------");
//                    System.out.println(minion.getMap().getMinionAt(row+dis,col));
//                    System.out.println("----------------------------------------------");
//                }
                else if(minion.onMap(row,col-dis) && !minion.getOwner().getMinion().contains(minion.getMap().getMinionAt(row,col-dis))){
                    System.out.println((row+dis) + " " + 5 + " " + (col-dis));
                    return dis*10+5;
                }
                else if(minion.onMap(row-dis,col-dis) && !minion.getOwner().getMinion().contains(minion.getMap().getMinionAt(row-dis,col-dis))){
                    System.out.println(6);
                    return dis*10+6;
                }
            }
        }
        return 0;
    }

    @Override
    public void prettyPrint(StringBuilder s) {

    }
}
