import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

interface WebSocketState {
    isConnected: boolean;
}

const initialState: WebSocketState = {
    isConnected: false,
};

const webSocketSlice = createSlice({
    name: "webSocket",
    initialState,
    reducers: {
        setWebSocketStatus: (state, action: PayloadAction<boolean>) => {
            state.isConnected = action.payload;
        }
    },
});

export const { setWebSocketStatus } = webSocketSlice.actions;
export default webSocketSlice.reducer;