import { createSlice, PayloadAction } from "@reduxjs/toolkit";

export interface CustomizeModalState {
    id: string; // เช่น "customize 1"
    isOpen: boolean;
}

const initialState: CustomizeModalState[] = [
    { id: "customize 1", isOpen: false },
    { id: "customize 2", isOpen: false },
    { id: "customize 3", isOpen: false },
    { id: "customize 4", isOpen: false },
    { id: "customize 5", isOpen: false },
];

const customizeModalSlice = createSlice({
    name: "customizeModal",
    initialState,
    reducers: {
        // ✅ toggle modal ตาม id
        toggleCustomize: (state, action: PayloadAction<string>) => {
            const item = state.find((modal) => modal.id === action.payload);
            if (item) {
                item.isOpen = !item.isOpen;
            }
        },

        // ✅ set เปิด/ปิด modal ตาม id
        setCustomize: (
            state,
            action: PayloadAction<{ id: string; isOpen: boolean }>
        ) => {
            const item = state.find((modal) => modal.id === action.payload.id);
            if (item) {
                item.isOpen = action.payload.isOpen;
            }
        },

        // ✅ set ใหม่ทั้ง array
        setAllCustomizes: (state, action: PayloadAction<CustomizeModalState[]>) => {
            return action.payload;
        },

        // ✅ reset ทั้งหมด
        resetCustomize: () => initialState,
    },
});

export const {
    toggleCustomize,
    setCustomize,
    setAllCustomizes,
    resetCustomize,
} = customizeModalSlice.actions;

export default customizeModalSlice.reducer;
