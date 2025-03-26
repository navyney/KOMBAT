package backend.WebSocketDTOs;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MinionConfigMessage {
    private String playerId;
    private List<MinionType> minions;

}
