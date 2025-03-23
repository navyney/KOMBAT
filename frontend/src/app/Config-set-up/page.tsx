"use client";

import { useRouter } from "next/navigation";
import Image from "next/image";
import { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
// import {useWebSocket} from "@/hooks/useWebsocket";
import { useAppDispatch, useAppSelector } from "@/stores/hook";
import { updateConfig, confirmConfig } from "@/stores/slices/configSlice";
import {RootState} from "@/stores/store";

export default function ConfigPage() {
    const router = useRouter();
    const dispatch = useAppDispatch();

    const config = useAppSelector((state) => state.config.config || {});
    const confirmedPlayers = useAppSelector((state) => state.config.confirmedPlayers);

    const [error, setError] = useState<string | null>(null);
    const [players, setPlayers] = useState(0);
    const [stompClient, setStompClient] = useState<Client | null>(null);
    // const { subscribe, sendMessage } = useWebSocket();

    const playerId = typeof window !== "undefined" ? localStorage.getItem("playerId") || "" : "";

    const isBothConfirmed = Object.values(confirmedPlayers ?? {}).length === 2 &&
        Object.values(confirmedPlayers ?? {}).every((val) => val);

    const youConfirmed = playerId && confirmedPlayers[playerId];
    const opponentConfirmed = Object.entries(confirmedPlayers).some(
        ([id, confirmed]) => id !== playerId && confirmed
    );

    useEffect(() => {
        const socket = new SockJS("http://localhost:8080/ws");
        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
        });

        client.onConnect = () => {
            console.log(`${playerId} is joined`);

            client.publish({
                destination: "/app/join-config-setup",
                body: JSON.stringify({playerId}),
            });

            client.subscribe("/topic/player-count", (message) => {
                const count = JSON.parse(message.body);
                setPlayers(count);
            });

            client.subscribe("/topic/config-update", (message) => {
                const newConfig = JSON.parse(message.body);
                dispatch(updateConfig(newConfig));
            });

            client.subscribe("/topic/config-confirmed", (message) => {
                const { playerId: confirmId } = JSON.parse(message.body);
                dispatch(confirmConfig(confirmId));
            });

            client.subscribe("/topic/navigate", (message) => {
                const { action, player1, player2 } = JSON.parse(message.body);

                console.log("🌐 Navigation from server:", { action, player1, player2 });
                if (action === "next") router.push("/select-type");
                else if (action === "back") router.push("/select-mode");
            });

            client.subscribe("/topic/config-reset-confirmed", () => {
                dispatch(confirmConfig("reset"));
            });

        };

        client.activate();
        setStompClient(client);

        return () => {
            client.deactivate();
        };
    }, [dispatch, router, playerId]);

    // useEffect(() => {
    //     sendMessage("/app/join-config-setup", { playerId });
    //
    //     const playerCountSub = subscribe("/topic/player-count", (message) => {
    //         const count = JSON.parse(message.body);
    //         setPlayers(count);
    //     });
    //
    //     const configUpdateSub = subscribe("/topic/config-update", (message) => {
    //         const newConfig = JSON.parse(message.body);
    //         dispatch(updateConfig(newConfig));
    //     });
    //
    //     const confirmSub = subscribe("/topic/config-confirmed", (message) => {
    //         const { playerId: confirmId } = JSON.parse(message.body);
    //         dispatch(confirmConfig(confirmId));
    //     });
    //
    //     const navSub = subscribe("/topic/navigate", (message) => {
    //         const action = message.body;
    //         if (action === "next") router.push("/select-type");
    //         else if (action === "back") router.push("/select-mode");
    //     });
    //
    //     const resetSub = subscribe("/topic/config-reset-confirmed", () => {
    //         dispatch(confirmConfig("reset"));
    //     });
    //
    //     return () => {
    //         playerCountSub?.unsubscribe();
    //         configUpdateSub?.unsubscribe();
    //         confirmSub?.unsubscribe();
    //         navSub?.unsubscribe();
    //         resetSub?.unsubscribe();
    //     };
    // }, [dispatch, router, playerId, sendMessage, subscribe]);


    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        const key = name as keyof typeof config;

        if (value === "" || (!isNaN(Number(value)) && Number(value) >= 0)) {
            const parsedValue = value === "" ? "" : parseFloat(value);
            const updatedConfig = { ...config, [key]: parsedValue };

            dispatch(updateConfig(updatedConfig));
            //dispatch(confirmConfig("reset"));

            if (stompClient?.connected) {
                stompClient.publish({
                    destination: "/app/config-update",
                    // body: JSON.stringify(updatedConfig),
                    body: JSON.stringify({ ...updatedConfig, playerId }),
                });
            }
        }
    };

    const handleConfirm = () => {
        if (playerId) {
            //dispatch(confirmConfig(playerId));
            stompClient?.publish({
                destination: "/app/config-confirmed",
                body: JSON.stringify({ playerId }),
            });
        }
    };

    const handleNext = () => {
        if (isBothConfirmed && stompClient?.connected) {
            localStorage.setItem("gameConfig", JSON.stringify(config));
            stompClient.publish({
                destination: "/topic/navigate",
                body: JSON.stringify({
                    action: "next",
                    playerId: playerId, // ✅ เพิ่ม playerId ของผู้ส่ง
                })
            });
        }
    };

    const handleBack = () => {
        if (stompClient?.connected) {
            stompClient.publish({
                destination: "/topic/navigate",
                body: JSON.stringify({
                    action: "back",
                    playerId: playerId // ✅ เช่นเดียวกัน
                })
            });
        }
    };


    // const handleConfirm = () => {
    //     if (playerId) {
    //         sendMessage("/app/config-confirmed", { playerId });
    //     }
    // };
    //
    // const handleNext = () => {
    //     if (isBothConfirmed) {
    //         localStorage.setItem("gameConfig", JSON.stringify(config));
    //         sendMessage("/topic/navigate", "next");
    //     }
    // };
    //
    // const handleBack = () => {
    //     sendMessage("/topic/navigate", "back");
    // };
    //
    // const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    //     const { name, value } = e.target;
    //     const key = name as keyof typeof config;
    //
    //     if (value === "" || (!isNaN(Number(value)) && Number(value) >= 0)) {
    //         const parsedValue = value === "" ? "" : parseFloat(value);
    //         const updatedConfig = { ...config, [key]: parsedValue };
    //
    //         dispatch(updateConfig(updatedConfig));
    //
    //         sendMessage("/app/config-update", { ...updatedConfig, playerId });
    //     }
    // };


    return (
        <main className="flex flex-col items-center justify-center min-h-screen bg-cover bg-center w-full h-full p-8"
              style={{ backgroundImage: "url('/image/config.png')" }}>

            <div className="bg-white p-8 rounded-lg shadow-lg w-full max-w-lg mt-16 space-y-4 space-x-5">
                <h1 className="text-xl font-bold text-center text-black">Set Up Your Game Configuration</h1>
                <p className="text-center text-gray-600">Players Connected: {players} / 2</p>
                {Object.keys(config).filter(key => key !== "playerId").map((key) => (
                    <div key={key} className="flex justify-between items-center">
                        <label className="font-medium capitalize text-black">{key.replace(/([A-Z])/g, ' $1')} :</label>
                        <input
                            type="number"
                            name={key}
                            value={config[key as keyof typeof config]}
                            onChange={handleChange}
                            className="border rounded p-2 w-32 text-center text-black"
                        />
                    </div>
                ))}

                <div className='flex justify-between mt-4'>
                    <div className='flex items-center gap-2'>
                        <div
                            className={`w-4 h-4 border-2 rounded ${youConfirmed ? 'bg-green-500 border-green-700' : 'bg-white'}`}/>
                        <span className='text-sm text-black'>You</span>
                    </div>
                    <div className='flex items-center gap-2'>
                        <div
                            className={`w-4 h-4 border-2 rounded ${opponentConfirmed ? 'bg-green-500 border-green-700' : 'bg-white'}`}/>
                        <span className='text-sm text-black'>Opponent</span>
                    </div>
                </div>

                {error && <p className="text-red-500 text-center font-bold">{error}</p>}

                <div className="mt-4 flex justify-end">
                    <button
                        onClick={handleConfirm}
                        className="mt-4 bg-blue-500 text-white py-2 px-10 rounded hover:bg-blue-700 transition"
                    >
                        Confirm Config
                    </button>
                </div>
            </div>

            <div
                onClick={handleBack}
                className="absolute cursor-pointer bottom-10 left-20"
            >
                <Image
                    src="/image/back-button.png"
                    alt="back"
                    width={150}
                    height={150}
                    className="hover:opacity-75 transition-opacity"
                />
            </div>

            <div
                onClick={isBothConfirmed ? handleNext : undefined}
                className={`absolute cursor-pointer bottom-10 right-20 transition-opacity ${
                    isBothConfirmed ? "hover:opacity-75" : "opacity-50 cursor-not-allowed"
                }`}
            >
                <Image
                    src="/image/next-button.png"
                    alt="next"
                    width={150}
                    height={150}
                />
            </div>
        </main>
    );
}