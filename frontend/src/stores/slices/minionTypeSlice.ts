import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface MinionType {
    id: number;
    name: string;
    def: number;
    strategy: string;
}

const initialState: MinionType[] = [
    {
        id: 1,
        name: "Pawn",
        def: 1,
        strategy: "move down"
    },
    {
        id: 2,
        name: "Rook",
        def: 1,
        strategy: "move down"
    },
    {
        id: 3,
        name: "Knight",
        def: 1,
        strategy: "move down"
    },
    {
        id: 4,
        name: "Bishop",
        def: 1,
        strategy: "move down"
    },
    {
        id: 5,
        name: "Queen",
        def: 1,
        strategy: "move down"
    },
];

const minionTypeSlice = createSlice({
    name: 'minions',
    initialState,
    reducers: {

        addMinion: (state, action: PayloadAction<MinionType>) => {
            state.push(action.payload);
        },

        removeMinion: (state, action: PayloadAction<number>) => {
            return state.filter(minion => minion.id !== action.payload);
        },

        updateMinion: (state, action: PayloadAction<MinionType>) => {
            const { id, def, strategy, name } = action.payload;
            const existingMinion = state.find(minion => minion.id === id);
            if (existingMinion) {
                existingMinion.name = name;
                existingMinion.def = def;
                existingMinion.strategy = strategy;
            }
        },
        resetMinion: (state, action: PayloadAction<number>) => {
            const id = action.payload;
            const index = state.findIndex(minion => minion.id === id);
            const defaultMinion = initialState.find(minion => minion.id === id);
            if (index !== -1 && defaultMinion) {
                state[index] = { ...defaultMinion };
            }
        }
    },
});

export const { addMinion, removeMinion, updateMinion, resetMinion } = minionTypeSlice.actions;
export const selectMinions = (state: { minions: MinionType[] }) => state.minions;
export default minionTypeSlice.reducer;
