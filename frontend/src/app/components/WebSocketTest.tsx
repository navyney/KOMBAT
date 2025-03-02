"use client";
import { useState } from "react";
import { useWebSocket } from "@/hooks/useWebSocket";

const WebSocketTest = () => {
    const { messages, sendMessage } = useWebSocket();
    const [input, setInput] = useState("");

    return (
        <div>
            <h2>WebSocket Test</h2>
            <ul>
                {messages.map((msg, index) => (
                    <li key={index}>{msg}</li>
                ))}
            </ul>
            <input
                type="text"
                value={input}
                onChange={(e) => setInput(e.target.value)}
            />
            <button onClick={() => { sendMessage(input); setInput(""); }}>Send</button>
        </div>
    );
};

export default WebSocketTest;

// for testing sending a message by websocket