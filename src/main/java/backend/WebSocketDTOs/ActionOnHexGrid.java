package backend.WebSocketDTOs;
import backend.KOMBOOD.entity.Player;
import backend.KOMBOOD.map.MapMap;
import lombok.Getter;

@Getter
public class ActionOnHexGrid {
private int curPlayer;
private int row;
private int col;
private String minion;

}
