package backend.WebSocketController;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/selectMode")
    @SendTo("/topic/mode")
    public String selectMode(String mode) {
        return mode;
    }
}