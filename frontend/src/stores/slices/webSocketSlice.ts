import { createSlice } from "@reduxjs/toolkit";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const SOCKET_URL = "http://localhost:8080/ws";

const webSocketSlice = createSlice({
    name: "webSocket",
    initialState: {
        client: null as Client | null,
        connected: false,
        data: null,
    },
    reducers: {
        connectWebSocket: (state) => {
            if (!state.client) {
                const stompClient = new Client({
                    webSocketFactory: () => new SockJS(SOCKET_URL),
                    reconnectDelay: 5000,
                    onConnect: () => {
                        stompClient.subscribe("/topic/config", (message) => {
                            state.data = JSON.parse(message.body);
                        });
                        stompClient.publish({
                            destination: "/app/join",
                            body: JSON.stringify({ type: "player_join" })
                        });
                        state.connected = true;
                    },
                });
                stompClient.activate();
                state.client = stompClient;
            }
        },
        sendWebSocketMessage: (state, action) => {
            if (state.client && state.connected) {
                state.client.publish({
                    destination: "/app/config",
                    body: JSON.stringify(action.payload),
                });
            }
        },
    },
});

export const { connectWebSocket, sendWebSocketMessage } = webSocketSlice.actions;
export default webSocketSlice.reducer;