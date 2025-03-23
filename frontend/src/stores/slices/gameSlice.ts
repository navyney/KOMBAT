import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface GameState {
    lockedMode: string | null;
    disableAll: boolean;
    roomFull: boolean;
}

const initialState: GameState = {
    lockedMode: null,
    disableAll: false,
    roomFull: false,
};

const gameSlice = createSlice({
    name: "game",
    initialState,
    reducers: {
        setLockedMode: (state, action: PayloadAction<string | null>) => {
            state.lockedMode = action.payload;
        },
        setDisableAll: (state, action: PayloadAction<boolean>) => {
            state.disableAll = action.payload;
        },
        setFull: (state, action: PayloadAction<boolean>) => {
            state.roomFull = action.payload;
        },
    },
});

export const { setLockedMode, setDisableAll, setFull } = gameSlice.actions;
export default gameSlice.reducer;
