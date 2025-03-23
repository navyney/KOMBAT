package backend.WebSocketDTOs;


import backend.KOMBOOD.strategy.Strategy;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MinionTypeData {
    private String name;
    private int def;
    private String strategy;
}
