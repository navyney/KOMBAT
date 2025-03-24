"use client";

import { useRouter } from "next/navigation";
import { useEffect } from "react";
import { useWebSocket } from "@/hooks/useWebsocket";
import { usePlayerId } from "@/hooks/usePlayerId";
import { useSelector } from "react-redux";
import { RootState } from "@/stores/store";
import {setDisableAll, setFull, setLockedMode} from "@/stores/slices/gameSlice";
import {useAppDispatch} from "@/stores/hook";

export default function SelectModePage() {
    const router = useRouter();
    const playerId = usePlayerId();
    const { connect, subscribe, sendMessage, isConnected } = useWebSocket();
    const dispatch = useAppDispatch();
    const disableAll = useSelector((state: RootState) => state.game.disableAll);
    const lockedMode = useSelector((state: RootState) => state.game.lockedMode);
    const roomFull = useSelector((state: RootState) => state.game.roomFull);

    useEffect(() => {
        if (!playerId || isConnected()) return;
        connect();
        //console.log("🎮 Player ID:", playerId);
    }, [playerId]);

    useEffect(() => {
        if (!playerId || !isConnected()) return;

        const subLockMode = subscribe("/topic/lock-mode", (message) => {
            const { selectedMode } = JSON.parse(message.body);
            dispatch(setLockedMode(selectedMode));
        });

        const subLockAll = subscribe("/topic/lock-all", () => {
            dispatch(setFull(true));
            dispatch(setDisableAll(true));
            dispatch(setLockedMode(null));
        });

        setTimeout(() => {
            sendMessage("/request-lock-status", { playerId });
        }, 100);

        return () => {
            subLockMode?.unsubscribe();
            subLockAll?.unsubscribe();
        };
    }, [playerId, isConnected]);

    const handleModeSelect = (mode: string) => {
        if (!playerId) return;
        sendMessage("/select-mode", { mode, playerId });

        if (mode === "pvb" || mode === "bvb") {
            router.push("/Not-pvp-config");
            dispatch(setDisableAll(true));
        } else if (mode === "pvp") {
            router.push("/Config-set-up");
        }
    };

    const isModeDisabled = (mode: string) => {
        if (disableAll || roomFull) return true;

        if (lockedMode === "pvp") {
            return mode !== "pvp";
        } else if (lockedMode === "pvb") {
            return mode !== "pvb";
        } else if (lockedMode === "bvb") {
            return mode !== "bvb";
        }

        return false;
    };

    return (
        <main className="flex flex-col items-center justify-center min-h-screen bg-orange-100 w-full h-full">
            <h1 className="text-4xl font-bold text-black mb-6">Select Game Mode</h1>

            <div className="grid grid-cols-3 gap-6">
                <button
                    onClick={() => handleModeSelect("pvp")}
                    disabled={roomFull || lockedMode === "pvb" || lockedMode === "bvb"}
                    className={`px-6 text-white py-2 rounded text-lg font-semibold transition-colors ${isModeDisabled("pvp") ? "bg-gray-400" : "bg-orange-500 hover:bg-orange-600"}`}
                >
                    Player vs Player
                </button>
                <button
                    onClick={() => handleModeSelect("pvb")}
                    disabled={roomFull || lockedMode === "pvp" || lockedMode === "bvb"}
                    className={`px-6 text-white py-2 rounded text-lg font-semibold transition-colors ${isModeDisabled("pvb") ? "bg-gray-400" : "bg-orange-500 hover:bg-orange-600"}`}
                >
                    Player vs Bot
                </button>
                <button
                    onClick={() => handleModeSelect("bvb")}
                    disabled={roomFull || lockedMode === "pvp" || lockedMode === "pvb"}
                    className={`px-6 text-white py-2 rounded text-lg font-semibold transition-colors ${isModeDisabled("bvb") ? "bg-gray-400" : "bg-orange-500 hover:bg-orange-600"}`}
                >
                    Bot vs Bot
                </button>

                {disableAll && (
                    <p className="text-red-600 font-semibold mt-4">
                        The game is already started or full
                    </p>
                )}
            </div>

            <button
                onClick={() => router.push("/")}
                className="mt-6 px-6 py-3 text-lg font-bold text-white bg-orange-500 rounded-lg shadow-md hover:bg-orange-400 transition"
            >
                BACK
            </button>
        </main>
    );
}
