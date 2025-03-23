import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface PlayerState {
    playerId: string | null;
    role: "player1" | "player2" | "spectator" | null;
}

const initialState: PlayerState = {
    playerId: null,
    role: null,
};

const playerSlice = createSlice({
    name: "player",
    initialState,
    reducers: {
        setPlayerId: (state, action: PayloadAction<string>) => {
            state.playerId = action.payload;
        },
        setRole: (state, action: PayloadAction<"player1" | "player2" | "spectator">) => {
            state.role = action.payload;
        },
        resetPlayer: () => initialState,
    },
});

export const { setPlayerId, setRole, resetPlayer } = playerSlice.actions;
export default playerSlice.reducer;
