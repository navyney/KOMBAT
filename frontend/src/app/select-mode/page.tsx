"use client";

import { useRouter } from "next/navigation";
import {use, useEffect, useState} from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import {useWebSocket} from "@/hooks/useWebsocket";

export default function SelectModePage() {
    const router = useRouter();
    const [client, setClient] = useState<Client | null>(null);
    const [disableAll, setDisableAll] = useState(false);
    const [lockedMode, setLockedMode] = useState<string | null>(null);
    const [roomFull, setFull] = useState(false);
    const { connect, sendMessage, subscribe } = useWebSocket();

    useEffect(() => {
        connect();
    }, [connect]);

    useEffect(() => {
        const storedLockedMode = sessionStorage.getItem("lockedMode");
        const storedRoomFull = sessionStorage.getItem("roomFull");

        if (storedLockedMode) {
            setLockedMode(storedLockedMode);
        } else {
            setLockedMode(null);
        }

        if (storedRoomFull === "true") {
            setDisableAll(true);
            setFull(true);
        } else {
            setDisableAll(false);
            setFull(false);
        }

        let playerId = localStorage.getItem("playerId");
        if (!playerId) {
            playerId = crypto.randomUUID();
            localStorage.setItem("playerId", playerId);
        }

        // ✅ subscribe topic
        const roleSub = subscribe("/topic/role-assigned", (message) => {
            const { role, playerId: targetId, disableButtons } = JSON.parse(message.body);
            const localId = sessionStorage.getItem("playerId")
            if (targetId === localId) {
                console.log(`${role} is joined`);
                sessionStorage.setItem("playerRole", role);
                setDisableAll(disableButtons);
            }
        });

        const lockModeSub = subscribe("/topic/lock-mode", (message) => {
            const { selectedMode } = JSON.parse(message.body);
            setLockedMode(selectedMode);
            sessionStorage.setItem("lockedMode", selectedMode);
        });

        const lockAllSub = subscribe("/topic/lock-all", () => {
            setDisableAll(true);
            setFull(true);
            sessionStorage.setItem("roomFull", "true");
        });

        // ✅ แจ้ง join และขอข้อมูลล่าสุด
        sendMessage("/app/request-lock-status", JSON.stringify({ playerId }));

        return () => {
            roleSub?.unsubscribe();
            lockModeSub?.unsubscribe();
            lockAllSub?.unsubscribe();
            // Clear lockedMode when leaving the page
            sessionStorage.removeItem("lockedMode");
            sessionStorage.removeItem("roomFull");
        };

    }, [sendMessage, subscribe]);

    const handleModeSelect = (mode: string) => {
        const playerId = localStorage.getItem("playerId");
        if (!playerId || disableAll || roomFull) return;

        sendMessage("/app/select-mode", { mode, playerId });
        router.push("/Config-set-up");
    };

    return (
        <main className="flex flex-col items-center justify-center min-h-screen bg-orange-100 w-full h-full">
            <h1 className="text-4xl font-bold text-black mb-6">Select Game Mode</h1>

            <div className={"grid grid-cols-3 gap-6"}>
                {/* PvP */}
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

                {/* PvB */}
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

                {/* BvB */}
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
