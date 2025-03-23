"use client";

import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { useWebSocket } from "@/hooks/useWebsocket";
import { setRole } from "@/stores/slices/playerSlice";
import { setDisableAll, setLockedMode, setFull } from "@/stores/slices/gameSlice";
import { usePlayerId } from "@/hooks/usePlayerId";

interface Props {
    children: React.ReactNode;
}

const WebSocketProvider: React.FC<Props> = ({ children }) => {
    const dispatch = useDispatch();
    const playerId = usePlayerId();
    const { connect, subscribe, isConnected, sendMessage } = useWebSocket();

    useEffect(() => {
        if (!playerId) return;
        if (!isConnected()) {
            connect();
        }
    }, [playerId, connect, isConnected]);

    useEffect(() => {
        if (!playerId || !isConnected()) return;

        sendMessage("/request-lock-status", { playerId });

        // const subRole = subscribe("/topic/role-assigned", (message) => {
        //     const { role, playerId: targetId, disableButtons } = JSON.parse(message.body);
        //     if (targetId === playerId) {
        //         dispatch(setRole(role));
        //         dispatch(setDisableAll(disableButtons));
        //     }
        // });
        //
        // const subLockMode = subscribe("/topic/lock-mode", (message) => {
        //     const { selectedMode } = JSON.parse(message.body);
        //     dispatch(setLockedMode(selectedMode));
        // });
        //
        // const subLockAll = subscribe("/topic/lock-all", () => {
        //     dispatch(setFull(true));
        //     dispatch(setDisableAll(true));
        //     dispatch(setLockedMode(null));
        // });

        const subRole = subscribe("/topic/role-assigned", (message) => {
            const { role, playerId: targetId, disableButtons } = JSON.parse(message.body);
            if (targetId === playerId) {
                sessionStorage.setItem("playerRole", role);
                dispatch(setRole(role));
                dispatch(setDisableAll(disableButtons));
            }
        });

        const subLockMode = subscribe("/topic/lock-mode", (message) => {
            const { selectedMode } = JSON.parse(message.body);
            // setLockedMode(selectedMode);
            // sessionStorage.setItem("lockedMode", selectedMode);
            dispatch(setLockedMode(selectedMode));
        });

        const subLockAll = subscribe("/topic/lock-all", () => {
            // setDisableAll(true);
            // setFull(true);
            // sessionStorage.setItem("roomFull", "true");
            dispatch(setDisableAll(true));
            dispatch(setFull(true));
        });

        return () => {
            subRole?.unsubscribe();
            subLockMode?.unsubscribe();
            subLockAll?.unsubscribe();
        };
    }, [playerId, isConnected, subscribe, sendMessage, dispatch]);

    return <>{children}</>;
};

export default WebSocketProvider;
