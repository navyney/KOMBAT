import java.util.HashMap;

public class Minion {
    private int row,col,z=0,Int,random;
    private int attackFactor,defenseFactor;
    private double budget,maxbudget;
    private String name;
    private HashMap<String,Long> hmIdentifier = new HashMap<>();

    public Minion(int row, int col, String name) {
        this.row = row;
        this.col = col;
        this.name = name;
    }
    public void assign(String identifier,long val){
        hmIdentifier.put(identifier,val);
    }
    public long getValueIdentifier(String identifier){
        return hmIdentifier.get(identifier);
    }
    public void move(int direction){
        if(direction==1){
            row+=1;
        }//up
        else if(direction==2){
            col+=1;
            row+=1;
        }//upright
        else if(direction==3){
            col+=1;
            row-=1;
        }//downright
        else if(direction==4){
            row-=1;
        }//down
        else if(direction==5){
            col-=1;
            row-=1;
        }//downleft
        else if(direction==6){
            col-=1;
            row+=1;
        }//upleft
        else{
            throw new IllegalArgumentException("Invalid direction");
        }
    }
    public int getCol(){
        return col;
    }
    public int getRow(){
        return row;
    }
    public void attack(int direction,long damage){
        if(direction==1){
            z+=damage;
            System.out.print("up " + z);
        }//up
        else if(direction==2){
            z+=damage;
            System.out.print("upright " + z);
        }//upright
        else if(direction==3){
            z+=damage;
            System.out.println("downright " + z);
        }//downright
        else if(direction==4){
            z+=damage;
            System.out.println("down " + z);
        }//down
        else if(direction==5){
            z+=damage;
            System.out.println("downleft " + z);
        }//downleft
        else if(direction==6){
            z+=damage;
            System.out.println("upright " + z);
        }//upleft
        else{
            throw new IllegalArgumentException("Invalid direction");
        }
    }
}
