'use client' ;

import { JSXElementConstructor, Key, ReactElement, ReactNode, ReactPortal, useState} from "react";
import {useWebSocket} from "@/hooks/useWebsocket";

const WebSocketTest = () => {
    // @ts-ignore
    const {messages = [], sendMessage} = useWebSocket();
    const [input, setInput] = useState("");

    return (
        <div>
            <h2>WebSocket Test</h2>
            <ul>
                {messages.map((msg: string | number | bigint | boolean | ReactElement<unknown, string | JSXElementConstructor<any>> | Iterable<ReactNode> | ReactPortal | Promise<string | number | bigint | boolean | ReactPortal | ReactElement<unknown, string | JSXElementConstructor<any>> | Iterable<ReactNode> | null | undefined> | null | undefined, index: Key | null | undefined) => (
                    <li key={index}>{msg}</li>
                ))}
            </ul>
            <input
                type="text"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                placeholder="Type a message..."
            />
            <button onClick={() => sendMessage(input)}>Send</button>
        </div>
    );
};

export default WebSocketTest;
