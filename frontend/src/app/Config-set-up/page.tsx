"use client";

import { useRouter } from "next/navigation";
import Image from "next/image";
import { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

export default function StartPage() {
    const router = useRouter();
    const [config, setConfig] = useState({
        spawnedCost: "" as number | "",
        hexPurchasedCost: "" as number | "",
        initialBudget: "" as number | "",
        initialHP: "" as number | "",
        turnBudget: "" as number | "",
        maxBudget: "" as number | "",
        interestPercentage: "" as number | "",
        maxTurn: "" as number | "",
        maxSpawn: "" as number | "",
    });

    const [error, setError] = useState<string | null>(null);
    const [isConfirmed, setIsConfirmed] = useState(false);
    const [players, setPlayers] = useState(0);
    const [stompClient, setStompClient] = useState<Client | null>(null);

    useEffect(() => {
        const socket = new SockJS("http://localhost:8080/ws");
        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
        });

        client.onConnect = () => {
            console.log("✅ Connected to WebSocket Server");

            client.publish({ destination: "/app/join-config-setup" });

            client.subscribe("/topic/player-count", (message) => {
                const count = JSON.parse(message.body);
                setPlayers(count);
                if (count > 2) {
                    alert("already have 2 players, cant join");
                    router.push("/");
                }
            });

            client.subscribe("/topic/config-update", (message) => {
                setConfig(JSON.parse(message.body));
            });

            client.subscribe("/topic/config-confirmed", (message) => {
                setConfig(JSON.parse(message.body));
                setIsConfirmed(true);
            });
        };

        client.activate();
        setStompClient(client);

        return () => {
            client.deactivate();
        };
    }, [router]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        if (value === "" || (!isNaN(Number(value)) && Number(value) >= 0)) {
            const updatedConfig = { ...config, [e.target.name]: value === "" ? "" : parseFloat(value) };
            setConfig(updatedConfig);
            if (stompClient && stompClient.connected) {
                stompClient.publish({
                    destination: "/app/config-update",
                    body: JSON.stringify(updatedConfig),
                });
            }
        }
        setIsConfirmed(false);
    };

    const handleConfirm = () => {
        const hasError = Object.values(config).some(value => value === "" || isNaN(Number(value)));
        if (hasError) {
            setError("Allow only numbers. Please check your inputs.");
            setIsConfirmed(false);
        } else {
            setError(null);
            setIsConfirmed(true);
            if (stompClient && stompClient.connected) {
                stompClient.publish({
                    destination: "/app/config-confirmed",
                    body: JSON.stringify(config),
                });
            }
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
        </main>
    );
}
