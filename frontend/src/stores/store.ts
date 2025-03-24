import { configureStore } from "@reduxjs/toolkit";
import userReducer from "./slices/onlineUsersSlice";
import webSocketReducer from "./slices/webSocketSlice";
import gameReducer from "./slices/gameSlice";
import playerReducer from "./slices/playerSlice";
import configReducer from "./slices/configSlice"
import minionTypeReducer from "./slices/minionTypeSlice";

export const store = configureStore({
    reducer: {
        user: userReducer,
        websocket: webSocketReducer,
        game: gameReducer,
        player: playerReducer,
        config: configReducer,
        miniontype: minionTypeReducer,
    },
    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({ serializableCheck: false }),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
export default store;