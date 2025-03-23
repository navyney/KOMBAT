// import { createSlice, PayloadAction } from "@reduxjs/toolkit";
//
// interface GameState {
//     playersCount: number;
//     gameConfig: {
//         spawnedCost: number | "";
//         hexPurchasedCost: number | "";
//         initialBudget: number | "";
//         initialHP: number | "";
//         turnBudget: number | "";
//         maxBudget: number | "";
//         interestPercentage: number | "";
//         maxTurn: number | "";
//         maxSpawn: number | "";
//     };
//     isConfigConfirmed: boolean;
// }
//
// const initialState: GameState = {
//     playersCount: 0,
//     gameConfig: {
//         spawnedCost: "",
//         hexPurchasedCost: "",
//         initialBudget: "",
//         initialHP: "",
//         turnBudget: "",
//         maxBudget: "",
//         interestPercentage: "",
//         maxTurn: "",
//         maxSpawn: "",
//     },
//     isConfigConfirmed: false,
// };
//
// const gameSlice = createSlice({
//     name: "game",
//     initialState,
//     reducers: {
//         setPlayersCount: (state, action: PayloadAction<number>) => {
//             state.playersCount = action.payload;
//         },
//         updateGameConfig: (state, action: PayloadAction<Partial<GameState["gameConfig"]>>) => {
//             state.gameConfig = { ...state.gameConfig, ...action.payload };
//         },
//         confirmConfig: (state) => {
//             state.isConfigConfirmed = true;
//         },
//     },
// });
//
// export const { setPlayersCount, updateGameConfig, confirmConfig } = gameSlice.actions;
// export default gameSlice.reducer;

// gameSlice.ts
import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface GameState {
    disableAll: boolean;
    lockedMode: string | null;
    roomFull: boolean;
}

const initialState: GameState = {
    disableAll: false,
    lockedMode: null,
    roomFull: false,
};

const gameSlice = createSlice({
    name: "game",
    initialState,
    reducers: {
        setDisableAll: (state, action: PayloadAction<boolean>) => {
            state.disableAll = action.payload;
        },
        setLockedMode: (state, action: PayloadAction<string | null>) => {
            state.lockedMode = action.payload;
        },
        setFull: (state, action: PayloadAction<boolean>) => {
            state.roomFull = action.payload;
        },
    },
});

export const { setDisableAll, setLockedMode, setFull } = gameSlice.actions;
export default gameSlice.reducer;