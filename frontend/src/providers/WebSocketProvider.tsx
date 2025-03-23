// // import React, { useEffect } from "react";
// // import { useDispatch } from "react-redux";
// // import { connectWebSocket } from "@/stores/slices/webSocketSlice";
// //
// // interface Props {
// //     children: React.ReactNode;
// // }
// //
// // const WebSocketProvider: React.FC<Props> = ({ children }) => {
// //     const dispatch = useDispatch();
// //
// //     useEffect(() => {
// //         // Connect WebSocket only once when app starts
// //         dispatch(connectWebSocket());
// //
// //         // Do NOT disconnect on unmount to persist WebSocket across pages
// //     }, [dispatch]);
// //
// //     return <>{children}</>;
// // };
// //
// // export default WebSocketProvider;
//
"use client";

import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { useWebSocket } from "@/hooks/useWebsocket";
import { setOnlineUsers } from "@/stores/slices/onlineUsersSlice";
import { setRole, resetPlayer } from "@/stores/slices/playerSlice";
import { setDisableAll, setLockedMode, setFull } from "@/stores/slices/gameSlice";

export const WebSocketProvider = ({ children }: { children: React.ReactNode }) => {
    const { connect, subscribe } = useWebSocket();
    const dispatch = useDispatch();

    useEffect(() => {
        connect();

        // Subscribe to /topic/onlineUsers to update online users count
        const onlineUsersSubscription = subscribe("/topic/onlineUsers", (message) => {
            dispatch(setOnlineUsers(Number(message.body)));
        });

        // Subscribe to /topic/role-assigned to handle role assignment
        const roleAssignedSubscription = subscribe("/topic/role-assigned", (message) => {
            const { role, playerId, disableButtons } = JSON.parse(message.body);
            if (playerId === localStorage.getItem("playerId")) {
                dispatch(setRole(role)); // Update player role in Redux
                dispatch(setDisableAll(disableButtons)); // Disable buttons if needed
            }
        });

        // Subscribe to /topic/lock-all to handle room full status
        const lockAllSubscription = subscribe("/topic/lock-all", () => {
            dispatch(setFull(true)); // Set room full status
            dispatch(setDisableAll(true)); // Disable all buttons
            dispatch(setLockedMode(null)); // Reset locked mode if needed
        });

        return () => {
            // Unsubscribe from all topics when component unmounts
            onlineUsersSubscription?.unsubscribe();
            roleAssignedSubscription?.unsubscribe();
            lockAllSubscription?.unsubscribe();
        };
    }, [connect, subscribe, dispatch]);

    return <>{children}</>;
};

export default WebSocketProvider;
