"use client";

import { useRouter } from "next/navigation";
import Image from "next/image";
import { useEffect } from "react";
import { useWebSocket } from "@/hooks/useWebsocket";
import { usePlayerId } from "@/hooks/usePlayerId";

export default function StartPage() {
    const router = useRouter();
    const {
        connect,
        sendMessage,
        subscribe,
        isConnected,
        unsubscribe,
    } = useWebSocket();
    const playerId = usePlayerId();

    useEffect(() => {
        // for debug player id
        console.log("🎮 Player ID:", playerId);
    }, [playerId]);

    useEffect(() => {
        if (!playerId) return;
        if (!isConnected()) connect();

        const subRole = subscribe("/topic/role-assigned", (message) => {
            const { role, playerId: targetId, disableButtons } = JSON.parse(message.body);
            if (targetId === playerId) {
                localStorage.setItem("playerRole", role);
                console.log("🎮 Player role:", role);
            }
        });

        const subLockAll = subscribe("/topic/lock-all", () => {
            localStorage.setItem("roomFull", "true");
        });

        const subLockMode = subscribe("/topic/lock-mode", (message) => {
            const { selectedMode } = JSON.parse(message.body);
            localStorage.setItem("lockedMode", selectedMode);
        });

        if (playerId) {
            console.log("📨 Sending request-lock-status with:", playerId);
            sendMessage("/request-lock-status", { playerId });
        }

        return () => {
            unsubscribe(subRole);
            unsubscribe(subLockAll);
            unsubscribe(subLockMode);
        };
    }, [playerId]);

    return (
        <main>
            <div
                className="flex flex-col items-center justify-center min-h-screen bg-cover bg-center w-full h-full"
                style={{
                    backgroundImage: "url('/image/Desktop - 1.png')",
                    backgroundPosition: "center",
                }}
            ></div>
            <div
                onClick={() => router.push("/select-mode")}
                className="absolute cursor-pointer bottom-80 left-[calc(33%+100px)]"
            >
                <Image
                    src="/image/startbutton.png"
                    alt="start"
                    width={450}
                    height={450}
                    className="hover:opacity-90 transition-opacity"
                />
            </div>
        </main>
    );
}
