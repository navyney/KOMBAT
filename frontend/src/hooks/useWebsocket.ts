import { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

const SERVER_URL = "http://localhost:8080/ws";

export const useWebSocket = () => {
    const [messages, setMessages] = useState<string[]>([]); // ✅ ให้ค่าเริ่มต้นเป็น Array

    useEffect(() => {
        const socket = new SockJS(SERVER_URL);
        const client = Stomp.over(socket);

        client.connect({}, () => {
            console.log("WebSocket Connected");

            client.subscribe("/topic/messages", (message) => {
                console.log("Received:", message.body);
                setMessages((prev) => [...prev, message.body]); // ✅ อัปเดตค่าเป็น Array เสมอ
            });
        });

        return () => {
            if (client) {
                client.disconnect(() => console.log("WebSocket Disconnected"));
            }
        };
    }, []);

    const sendMessage = (msg: string) => {
        console.log("Sending message:", msg);
    };

    return { messages, sendMessage };
};
