package backend.WebSocketController;

import backend.KOMBOOD.entity.MinionType;
import backend.KOMBOOD.error.EvalError;
import backend.KOMBOOD.error.LexicalError;
import backend.KOMBOOD.parser.ExprTokenizer;
import backend.KOMBOOD.parser.StatementParser;
import backend.KOMBOOD.strategy.Strategy;
import backend.WebSocketDTOs.MinionTypeData;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

public class MinionTypeController {

    @MessageMapping("/createMinion")
    @SendTo("/topic/minionUpdates")
    public MinionType createMinion(MinionTypeData minionData) throws LexicalError, EvalError {
        String name = minionData.getName();
        int def = minionData.getDef();
        String s = minionData.getStrategy();

        StatementParser q = new StatementParser(new ExprTokenizer(s));
        Strategy strategy = q.parse();

        System.out.println("Minion created!");
        System.out.println("Name: " + name);
        System.out.println("Defense: " + def);
        System.out.println("Strategy: " + strategy);

        return new MinionType(name, def, strategy);
    }
}
