import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface selectionState {
    id: string;
    isSelected: boolean;
}

const initialState: selectionState[] = [
    {
        id: "select 1",
        isSelected: false,
    },
    {
        id: "select 2",
        isSelected: false,
    },
    {
        id: "select 3",
        isSelected: false,
    },
    {
        id: "select 4",
        isSelected: false,
    },
    {
        id: "select 5",
        isSelected: false,
    },
]

const selectionStateSlice = createSlice({
    name:"selectionState",
    initialState,
    reducers: {
        // ✅ toggle ปุ่มตาม id
        toggleSelection: (state, action: PayloadAction<string>) => {
            const item = state.find(btn => btn.id === action.payload);
            if (item) {
                item.isSelected = !item.isSelected;
            }
        },

        // ✅ ตั้งค่าตรงๆให้ปุ่มใดปุ่มหนึ่ง
        setSelection: (
            state,
            action: PayloadAction<{ id: string; isSelected: boolean }>
        ) => {
            const item = state.find(btn => btn.id === action.payload.id);
            if (item) {
                item.isSelected = action.payload.isSelected;
            }
        },

        // ✅ ตั้งค่าทั้งหมดจาก array ใหม่ (ใช้ตอนรับจาก backend)
        setAllSelections: (state, action: PayloadAction<selectionState[]>) => {
            return action.payload;
        },

        // ✅ reset กลับค่าเริ่มต้น
        resetSelections: () => initialState,
    },
});

export const {
    toggleSelection,
    setSelection,
    setAllSelections,
    resetSelections,
} = selectionStateSlice.actions;

export default selectionStateSlice.reducer;