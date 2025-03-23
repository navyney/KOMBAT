package backend.WebSocketDTOs;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
public class WebSocketDTO implements Serializable {

    @Getter
    @Setter
    private String playerId;

    @Min(0) private int spawnedCost;
    @Min(0) private int hexPurchasedCost;
    @Min(0) private int initialBudget;
    @Min(0) private int initialHP;
    @Min(0) private int turnBudget;
    @Min(0) private int maxBudget;
    @Min(0) private int interestPercentage;
    @Min(0) private int maxTurn;
    @Min(0) private int maxSpawn;

    @Override
    public String toString() {
        return "WebSocketDTO{" +
                "playerId='" + playerId + '\'' +
                ", spawnedCost=" + spawnedCost +
                ", hexPurchasedCost=" + hexPurchasedCost +
                ", initialBudget=" + initialBudget +
                ", initialHP=" + initialHP +
                ", turnBudget=" + turnBudget +
                ", maxBudget=" + maxBudget +
                ", interestPercentage=" + interestPercentage +
                ", maxTurn=" + maxTurn +
                ", maxSpawn=" + maxSpawn +
                '}';
    }
}
