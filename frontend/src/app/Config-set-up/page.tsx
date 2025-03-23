"use client";

import { useRouter } from "next/navigation";
import Image from "next/image";
import { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import { useAppDispatch, useAppSelector } from "@/stores/hook";
import { updateConfig, confirmConfig } from "@/stores/slices/configSlice";

export default function ConfigPage() {
    const router = useRouter();
    const dispatch = useAppDispatch();

    const config = useAppSelector((state) => state.config.config || {});
    const confirmedPlayers = useAppSelector((state) => state.config.confirmedPlayers);

    const [error, setError] = useState<string | null>(null);
    const [players, setPlayers] = useState(0);
    const [stompClient, setStompClient] = useState<Client | null>(null);

    const playerId = typeof window !== "undefined" ? localStorage.getItem("playerId") || "" : "";

    const isBothConfirmed = Object.values(confirmedPlayers ?? {}).length === 2 &&
        Object.values(confirmedPlayers ?? {}).every((val) => val);

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
                dispatch(confirmConfig("reset"));
            });

            client.subscribe("/topic/config-confirmed", (message) => {
                const { playerId: confirmId } = JSON.parse(message.body);
                dispatch(confirmConfig(confirmId));
            });

            client.subscribe("/topic/navigate", (message) => {
                const action = message.body;
                if (action === "next") router.push("/select-type");
                else if (action === "back") router.push("/select-mode");
            });
        };

        client.activate();
        setStompClient(client);

        return () => {
            client.deactivate();
        };
    }, [dispatch, router, playerId]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        if (value === "" || (!isNaN(Number(value)) && Number(value) >= 0)) {
            const updatedConfig = { ...config, [e.target.name]: value === "" ? "" : parseFloat(value) };
            dispatch(updateConfig(updatedConfig));
            dispatch(confirmConfig("reset"));

            if (stompClient?.connected) {
                stompClient.publish({
                    destination: "/app/config-update",
                    body: JSON.stringify(updatedConfig),
                });
            }
        }
    };

    const handleConfirm = () => {
        const hasError = Object.values(config).some(value => value === "" || isNaN(Number(value)));
        if (hasError) {
            setError("Allow only numbers. Please check your inputs.");
        } else {
            setError(null);
            if (stompClient?.connected) {
                stompClient.publish({
                    destination: "/app/config-confirmed",
                    body: JSON.stringify({ ...config, playerId }),
                });
            }
        }
    };

    const handleNext = () => {
        if (isBothConfirmed && stompClient?.connected) {
            localStorage.setItem("gameConfig", JSON.stringify(config));
            stompClient.publish({ destination: "/topic/navigate", body: "next" });
        }
    };

    const handleBack = () => {
        if (stompClient?.connected) {
            stompClient.publish({ destination: "/topic/navigate", body: "back" });
        }
    };

    return (
        <main className="flex flex-col items-center justify-center min-h-screen bg-cover bg-center w-full h-full p-8"
              style={{ backgroundImage: "url('/image/config.png')" }}>

            <div className="bg-white p-8 rounded-lg shadow-lg w-full max-w-lg mt-16 space-y-4 space-x-5">
                <h1 className="text-xl font-bold text-center">Set Up Your Game Configuration</h1>
                <p className="text-center text-gray-600">Players Connected: {players} / 2</p>
                {Object.keys(config).map((key) => (
                    <div key={key} className="flex justify-between items-center">
                        <label className="font-medium capitalize">{key.replace(/([A-Z])/g, ' $1')} :</label>
                        <input
                            type="number"
                            name={key}
                            value={config[key as keyof typeof config]}
                            onChange={handleChange}
                            className="border rounded p-2 w-32 text-center"
                        />
                    </div>
                ))}

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
