"use client";

import { useRouter } from "next/navigation";
import { useEffect } from "react";
import { useWebSocket } from "@/hooks/useWebsocket";
import { usePlayerId } from "@/hooks/usePlayerId";
import { useSelector } from "react-redux";
import { RootState } from "@/stores/store";

export default function SelectModePage() {
    const router = useRouter();
    const playerId = usePlayerId();
    const { connect, sendMessage, isConnected } = useWebSocket();

    const disableAll = useSelector((state: RootState) => state.game.disableAll);
    const lockedMode = useSelector((state: RootState) => state.game.lockedMode);
    const roomFull = useSelector((state: RootState) => state.game.roomFull);

    useEffect(() => {
        if (!playerId || isConnected()) return;
        connect();
    }, [playerId, connect, isConnected]);

    const handleModeSelect = (mode: string) => {
        if (!isConnected() || disableAll || roomFull) {
            console.log("⛔️ Cannot select mode. Room is full or disabled.");
            return;
        }

        sendMessage("/select-mode", { mode, playerId });
        router.push("/Config-set-up");
    };

    return (
        <main className="flex flex-col items-center justify-center min-h-screen bg-orange-100 w-full h-full">
            <h1 className="text-4xl font-bold text-black mb-6">Select Game Mode</h1>

            <div className="grid grid-cols-3 gap-6">
                <button
                    onClick={() => handleModeSelect("pvp")}
                    className={`px-6 py-3 rounded text-white text-lg font-semibold transition-colors ${
                        disableAll || (lockedMode && lockedMode !== "pvp")
                            ? "bg-gray-400 cursor-not-allowed"
                            : "bg-orange-500 hover:bg-orange-400"
                    }`}
                    disabled={disableAll || (lockedMode !== null && lockedMode !== "pvp")}
                >
                    Player vs Player
                </button>

                <button
                    onClick={() => handleModeSelect("pvb")}
                    className={`px-6 py-3 rounded text-white text-lg font-semibold transition-colors ${
                        disableAll || lockedMode !== null
                            ? "bg-gray-400 cursor-not-allowed"
                            : "bg-orange-500 hover:bg-orange-400"
                    }`}
                    disabled={disableAll || lockedMode !== null}
                >
                    Player vs Bot
                </button>

                <button
                    onClick={() => handleModeSelect("bvb")}
                    className={`px-6 py-3 rounded text-white text-lg font-semibold transition-colors ${
                        disableAll || lockedMode !== null
                            ? "bg-gray-400 cursor-not-allowed"
                            : "bg-orange-500 hover:bg-orange-400"
                    }`}
                    disabled={disableAll || lockedMode !== null}
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
