public class Minion {
    private int x=0,y=0;

    public void move(int direction){
        if(direction==1){
            y+=1;
        }
        else if(direction==2){
            x+=1;
            y+=1;
        }
        else if(direction==3){
            x+=1;
            y-=1;
        }
        else if(direction==4){
            y-=1;
        }
        else if(direction==5){
            x-=1;
            y-=1;
        }
        else if(direction==6){
            x-=1;
            y+=1;
        }
        else{
            x+=10;
        }
    }
}
