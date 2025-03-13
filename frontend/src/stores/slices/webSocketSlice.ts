import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { Client, Message } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { RootState } from "@/stores/store";

const SOCKET_URL = "http://localhost:8080/ws";

interface WebSocketState {
    client: Client | null;
    connected: boolean;
    data: any;
}

const initialState: WebSocketState = {
    client: null,
    connected: false,
    data: null,
};

const webSocketSlice = createSlice({
    name: "webSocket",
    initialState,
    reducers: {
        connectWebSocket: (state) => {
            if (!state.client) {
                const stompClient = new Client({
                    webSocketFactory: () => new SockJS(SOCKET_URL),
                    reconnectDelay: 5000,
                    onConnect: () => {
                        stompClient.subscribe("/topic/config", (message: Message) => {
                            state.data = JSON.parse(message.body);
                        });
                        stompClient.publish({
                            destination: "/app/join",
                            body: JSON.stringify({ type: "player_join" })
                        });
                        state.connected = true;
                    },
                    onDisconnect: () => {
                        state.connected = false;
                        state.client = null;
                    },
                });
                stompClient.activate();
                state.client = stompClient;
            }
        },
        sendWebSocketMessage: (state, action: PayloadAction<any>) => {
            if (state.client && state.connected) {
                state.client.publish({
                    destination: "/app/config",
                    body: JSON.stringify(action.payload),
                });
            }
        },
        setWebSocketClient: (state, action: PayloadAction<Client | null>) => {
            state.client = action.payload;
        },
        setConnectionStatus: (state, action: PayloadAction<boolean>) => {
            state.connected = action.payload;
        },
    },
});

export const {
    connectWebSocket,
    sendWebSocketMessage,
    setWebSocketClient,
    setConnectionStatus
} = webSocketSlice.actions;

export const selectWebsocket = (state: RootState) => state.websocket;
export default webSocketSlice.reducer;