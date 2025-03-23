import { createSlice, PayloadAction } from "@reduxjs/toolkit";

export interface GameConfig {
    spawnedCost: number;
    hexPurchasedCost: number;
    initialBudget: number;
    initialHP: number;
    turnBudget: number;
    maxBudget: number;
    interestPercentage: number;
    maxTurn: number;
    maxSpawn: number;
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
            state.confirmedBy = []; // reset confirmed status when config is updated
        },
        confirmConfig: (state, action: PayloadAction<string | "reset">) => {
            if (action.payload === "reset") {
                state.confirmedPlayers = {};
            } else {
                const playerId = action.payload;
                state.confirmedPlayers[playerId] = true;
            }
        },
        resetConfigConfirmation: (state) => {
            state.confirmedBy = [];
        },
    },
});

export const { updateConfig, confirmConfig, resetConfigConfirmation } = configSlice.actions;
export default configSlice.reducer;