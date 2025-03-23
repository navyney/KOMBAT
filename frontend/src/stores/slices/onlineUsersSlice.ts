import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface OnlineUsersState {
    count: number;
}

const initialState: OnlineUsersState = {
    count: 0,
};

const onlineUsersSlice = createSlice({
    name: "onlineUsers",
    initialState,
    reducers: {
        setOnlineUsers: (state, action: PayloadAction<number>) => {
            state.count = action.payload;
        },
    },
});

export const { setOnlineUsers } = onlineUsersSlice.actions;
export default onlineUsersSlice.reducer;
