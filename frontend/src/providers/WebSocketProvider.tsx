"use client";

import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { useWebSocket } from "@/hooks/useWebsocket";
import { setOnlineUsers } from "@/stores/slices/onlineUsersSlice";

export const WebSocketProvider = ({ children }: { children: React.ReactNode }) => {
    const { connect, subscribe } = useWebSocket();
    const dispatch = useDispatch();

    useEffect(() => {
        connect();

        const subscription = subscribe("/topic/onlineUsers", (message) => {
            dispatch(setOnlineUsers(Number(message.body)));
        });

        return () => {
            subscription?.unsubscribe();
        };
    }, [connect, subscribe, dispatch]);

    return <>{children}</>;
};

export default WebSocketProvider;
