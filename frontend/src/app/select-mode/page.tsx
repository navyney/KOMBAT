"use client";

import { useRouter } from "next/navigation";
import {use, useEffect, useState} from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

export default function SelectModePage() {
    const router = useRouter();
    const [client, setClient] = useState<Client | null>(null);
    const [disableAll, setDisableAll] = useState(false);
    const [lockedMode, setLockedMode] = useState<string | null>(null);
    const [roomFull, setFull] = useState(false);

    useEffect(() => {
        const storedLockedMode = sessionStorage.getItem("lockedMode");
        const storedRoomFull = sessionStorage.getItem("roomFull");

        if (storedLockedMode) {
            setLockedMode(storedLockedMode);
        } else {
            setLockedMode(null); // Ensure lockedMode is null if not set
        }

        if (storedRoomFull === "true") {
            setDisableAll(true);
            setFull(true);
        } else {
            setDisableAll(false);
            setFull(false);
        }

        let playerId = sessionStorage.getItem("playerId");
        if (!playerId) {
            playerId = crypto.randomUUID();
            sessionStorage.setItem("playerId", playerId);
        }

        const socket = new SockJS("http://localhost:8080/ws");
        const stompClient = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            onConnect: () => {
                console.log("✅ Connected to WebSocket successfully");
                stompClient.subscribe("/topic/role-assigned", (message) => {
                    const { role, playerId: targetId, disableButtons } = JSON.parse(message.body);
                    const localId = sessionStorage.getItem("playerId");
                    if (targetId === localId) {
                        console.log(`${role} is joined`);
                        sessionStorage.setItem("playerRole", role);
                        setDisableAll(disableButtons);
                    }
                });

                stompClient.subscribe("/topic/lock-mode", (message) => {
                    const { selectedMode } = JSON.parse(message.body);
                    setLockedMode(selectedMode);
                    sessionStorage.setItem("lockedMode", selectedMode);
                });

                stompClient.subscribe("/topic/lock-all", () => {
                    setDisableAll(true);
                    setFull(true);
                    sessionStorage.setItem("roomFull", "true");
                });

                // Request current state when connecting
                stompClient.publish({
                    destination: "/app/request-lock-status",
                    body: JSON.stringify({ playerId }),
                });
            },
            onDisconnect: () => {
                console.log("⛔️ Disconnected from WebSocket");
            },
        });

        stompClient.activate();
        setClient(stompClient);

        return () => {
            stompClient.deactivate();
            // Clear lockedMode when leaving the page
            sessionStorage.removeItem("lockedMode");
            sessionStorage.removeItem("roomFull");
        };
    }, []);

    const handleModeSelect = (mode: string) => {
        if (!client || !client.connected || disableAll || roomFull) {
            console.log("⛔️ Cannot select mode. Room is full or disabled.");
            return;
        }

        const playerId = localStorage.getItem("playerId");
        if (!playerId) return;

        client.publish({
            destination: "/app/select-mode",
            body: JSON.stringify({ mode, playerId }),
        });

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
