import { createSlice, PayloadAction } from "@reduxjs/toolkit";

export interface GameConfig {
    spawnedCost: number | "";
    hexPurchasedCost: number | "";
    initialBudget: number | "";
    initialHP: number | "";
    turnBudget: number | "";
    maxBudget: number | "";
    interestPercentage: number | "";
    maxTurn: number | "";
    maxSpawn: number | "";
}

interface ConfigState {
    confirmedPlayers: Record<string, boolean>;
    config: GameConfig;
    confirmedBy: string[]; // array of playerIds

    // confirmed: Record<string, boolean>; // key: playerId, value: isConfirmed
}

const initialState: ConfigState = {
    config: {
        spawnedCost: 0,
        hexPurchasedCost: 0,
        initialBudget: 0,
        initialHP: 0,
        turnBudget: 0,
        maxBudget: 0,
        interestPercentage: 0,
        maxTurn: 0,
        maxSpawn: 0,
    },
    confirmedBy: [],
    confirmedPlayers: {},
};

const configSlice = createSlice({
    name: "config",
    initialState,
    reducers: {
        updateConfig: (state, action: PayloadAction<GameConfig>) => {
            state.config = action.payload;
        },
        confirmConfig: (state, action: PayloadAction<string | "reset" | { type: "unconfirm", playerId: string }>) => {
            if (action.payload === "reset") {
                state.confirmedPlayers = {};
            } else if (typeof action.payload === "object" && action.payload.type === "unconfirm") {
                delete state.confirmedPlayers[action.payload.playerId];
            } else if (typeof action.payload === "string") {
                state.confirmedPlayers[action.payload] = true;
            }
        },
        resetConfigConfirmation: (state) => {
            state.confirmedBy = [];
        },
    },
});

export const { updateConfig, confirmConfig, resetConfigConfirmation } = configSlice.actions;
export default configSlice.reducer;