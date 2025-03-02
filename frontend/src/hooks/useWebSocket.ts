import { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

const SERVER_URL = "http://localhost:8080/ws";

export const useWebSocket = () => {
    const [stompClient, setStompClient] = useState<Stomp.Client | null>(null);
    const [messages, setMessages] = useState<string[]>([]);

    useEffect(() => {
        const socket = new SockJS(SERVER_URL);
        const client = Stomp.over(socket);

        client.connect({}, () => {
            console.log("WebSocket Connected");
            setStompClient(client);

            client.subscribe("/topic/messages", (message) => {
                setMessages((prev) => [...prev, message.body]);
            });
        });

        return () => {
            if (client.connected) {
                client.disconnect(() => console.log("WebSocket Disconnected"));
            }
        };
    }, []);

    const sendMessage = (msg: string) => {
        if (stompClient && stompClient.connected) {
            stompClient.send("/app/chat", {}, msg);
        }
    };

    return { messages, sendMessage };
};
