"use client";

import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { useWebSocket } from "@/hooks/useWebsocket";
import {resetPlayer, setRole} from "@/stores/slices/playerSlice";
import {setDisableAll, setLockedMode, setFull, resetGame} from "@/stores/slices/gameSlice";
import { usePlayerId } from "@/hooks/usePlayerId";
import {resetConfig} from "@/stores/slices/configSlice";
import {useRouter} from "next/navigation";
import { usePersistentPlayerId } from "@/hooks/usePersistentPlayerId";

interface Props {
    children: React.ReactNode;
}

const WebSocketProvider: React.FC<Props> = ({ children }) => {
    const dispatch = useDispatch();
    const playerId = usePersistentPlayerId();;
    const { connect, subscribe, isConnected, sendMessage } = useWebSocket();
    const router = useRouter();

    useEffect(() => {
        if (!playerId) return;
        if (!isConnected()) {
            console.log("🌐 Attempting to connect WebSocket...");
            connect();
        }
    }, [playerId]);

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
                console.log("🎮 Role assigned:", role);
            }
        });

        const subLockMode = subscribe("/topic/lock-mode", (message) => {
            const { selectedMode } = JSON.parse(message.body);
            dispatch(setLockedMode(selectedMode));
            console.log("🔒 Locked mode set:", selectedMode)
        });

        const subLockAll = subscribe("/topic/lock-all", () => {
            // setDisableAll(true);
            // setFull(true);
            // sessionStorage.setItem("roomFull", "true");
            dispatch(setDisableAll(true));
            dispatch(setFull(true));
            console.log("🔒 Room full - all controls disabled");
        });

        const subNav = subscribe("/topic/navigate", (message) => {
            const action = message.body;
            if (action === "back") {
                dispatch(resetPlayer());
                dispatch(resetGame());
                dispatch(resetConfig());
                //router.push("/select-mode");
                window.location.href = "/select-mode";
            } else if (action === "start") {
                dispatch(resetPlayer());
                dispatch(resetGame());
                dispatch(resetConfig());
                //router.push("/");
                window.location.href = "/";
            } else if (action === "next") {
                window.location.href = "/select-type";
            }
        });

        return () => {
            subRole?.unsubscribe();
            subLockMode?.unsubscribe();
            subLockAll?.unsubscribe();
            subNav?.unsubscribe();
        };
    }, [playerId, isConnected, subscribe]);

    return <>{children}</>;
};

export default WebSocketProvider;
