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

    @Getter
    @Setter
    @Min(0) private int spawnedCost;

    @Getter
    @Setter
    @Min(0) private int hexPurchasedCost;
    @Getter
    @Setter
    @Min(0) private int initialBudget;
    @Getter
    @Setter
    @Min(0) private int initialHP;
    @Getter
    @Setter
    @Min(0) private int turnBudget;
    @Getter
    @Setter
    @Min(0) private int maxBudget;
    @Getter
    @Setter
    @Min(0) private int interestPercentage;
    @Getter
    @Setter
    @Min(0) private int maxTurn;
    @Getter
    @Setter
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
