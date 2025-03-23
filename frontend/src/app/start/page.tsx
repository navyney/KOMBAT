"use client";

import { useRouter } from "next/navigation";
import Image from "next/image";
import { useEffect } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

export default function StartPage() {
    const router = useRouter();

    useEffect(() => {
        let playerId = localStorage.getItem("playerId");
        if (!playerId) {
            playerId = crypto.randomUUID();
            localStorage.setItem("playerId", playerId);
        }

        const socket = new SockJS("http://localhost:8080/ws");
        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
        });

        client.onConnect = () => {
            client.publish({
                destination: "/app/join-game",
                body: JSON.stringify({ playerId }),
            });

            // ✅ Subscribe เพื่อรับ role
            client.subscribe("/topic/role-assigned", (message) => {
                const { role, playerId: targetId, disableButtons } = JSON.parse(message.body);
                if (targetId === playerId) {
                    localStorage.setItem("playerRole", role);
                }
            });

            // ✅ Subscribe เพื่อ sync lock status เผื่อหน้า select-mode ต้องใช้
            client.subscribe("/topic/lock-all", () => {
                localStorage.setItem("roomFull", "true");
            });

            client.subscribe("/topic/lock-mode", (message) => {
                const { selectedMode } = JSON.parse(message.body);
                localStorage.setItem("lockedMode", selectedMode);
            });

            // ✅ Sync lock state แบบ request
            client.publish({
                destination: "/app/request-lock-status",
                body: JSON.stringify({ playerId }),
            });
        };

        client.activate();
    }, []);

    // useEffect(() => {
    //     const socket = new SockJS("http://localhost:8080/ws");
    //     const client = new Client({
    //         webSocketFactory: () => socket,
    //         reconnectDelay: 5000,
    //     });
    //
    //     client.onConnect = () => {
    //         client.publish({
    //             destination: "/app/join-game" ,
    //             body: JSON.stringify({ playerId: "?" })
    //         });
    //
    //         client.subscribe("/topic/role-assigned", (message) => {
    //             const { role, rejected } = JSON.parse(message.body);
    //             if (rejected) {
    //                 router.push("/game-started");
    //             } else {
    //                 localStorage.setItem("playerId", role);
    //             }
    //         });
    //     };
    //     client.activate();
    // }, [router]);

    return (
        <main>
            <div
                className="flex flex-col items-center justify-center min-h-screen bg-cover bg-center w-full h-full"
                style={{
                backgroundImage: "url('/image/Desktop - 1.png')",
                //backgroundSize: "1920px 1080px",
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
