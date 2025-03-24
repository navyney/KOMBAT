package backend.WebSocketDTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MinionType {
    private int id;
    private String name;
    private int def;
    private String strategy;
}