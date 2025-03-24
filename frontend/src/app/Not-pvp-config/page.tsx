"use client";

import { useEffect, useState } from "react";
import { useAppDispatch, useAppSelector } from "@/stores/hook";
import {confirmConfig, updateConfig} from "@/stores/slices/configSlice";
import { useWebSocket } from "@/hooks/useWebsocket";
import { usePlayerId } from "@/hooks/usePlayerId";
import { resetPlayer } from "@/stores/slices/playerSlice";
import { resetConfig } from "@/stores/slices/configSlice";
import { resetGame } from "@/stores/slices/gameSlice";
import Image from "next/image";

export default function NotPVPConfigPage() {
    const dispatch = useAppDispatch();
    const { subscribe, sendMessage, connect, isConnected, unsubscribe } = useWebSocket();
    const config = useAppSelector((state) => state.config.config || {});
    const [players, setPlayers] = useState(0);
    const playerId = usePlayerId();
    const [ youConfirmed , setYouConfirmed ] = useState<boolean | null>(null) ;
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (!playerId) return;
        if (!isConnected()) connect();

        const subCount = subscribe("/topic/player-count", (message) => {
            const count = JSON.parse(message.body);
            setPlayers(count);
        });

        const subUpdate = subscribe("/topic/config-update", (message) => {
            const newConfig = JSON.parse(message.body);
            dispatch(updateConfig(newConfig));
        });

        const subConfirm = subscribe("/topic/config-confirmed", (message) => {
            const { playerId: confirmId } = JSON.parse(message.body);
            dispatch(confirmConfig(confirmId));
        });

        const subNav = subscribe("/topic/navigate", (message) => {
            const action = message.body;
            if (action === "start") {
                dispatch(resetPlayer());
                dispatch(resetGame());
                dispatch(resetConfig());
                window.location.href = "/";
            } else if (action === "back") {
                dispatch(resetPlayer());
                dispatch(resetGame());
                dispatch(resetConfig());
                window.location.href = "/select-mode";
            } else if (action === "next") {
                window.location.href = "/Not-pvp-select-type";
            }
        });

        const subReset = subscribe("/topic/mode-reset", () => {
            dispatch(resetPlayer());
            dispatch(resetGame());
            dispatch(resetConfig());
            console.log("🔃 Received global reset from server");
            window.location.href = "/select-mode";
        });

        sendMessage("/join-config-setup", { playerId });

        return () => {
            unsubscribe(subCount);
            unsubscribe(subUpdate);
            unsubscribe(subConfirm);
            unsubscribe(subNav);
            unsubscribe(subReset);
        };
    }, [dispatch, playerId]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        const key = name as keyof typeof config;

        if (value === "" || (!isNaN(Number(value)) && Number(value) >= 0)) {
            const parsedValue = value === "" ? 0 : parseFloat(value);
            const updatedConfig = { ...config, [key]: parsedValue };

            dispatch(updateConfig(updatedConfig));

            const sanitizedConfig = {
                ...updatedConfig,
                spawnedCost: Number(updatedConfig.spawnedCost || 0),
                hexPurchasedCost: Number(updatedConfig.hexPurchasedCost || 0),
                initialBudget: Number(updatedConfig.initialBudget || 0),
                initialHP: Number(updatedConfig.initialHP || 0),
                turnBudget: Number(updatedConfig.turnBudget || 0),
                maxBudget: Number(updatedConfig.maxBudget || 0),
                interestPercentage: Number(updatedConfig.interestPercentage || 0),
                maxTurn: Number(updatedConfig.maxTurn || 0),
                maxSpawn: Number(updatedConfig.maxSpawn || 0),
                playerId,
            };

            sendMessage("/config-update", sanitizedConfig);
            console.log("📤 Sent config:", sanitizedConfig);
        }
    };

    const handleBack = () => {
        sendMessage("/navigate", "back");
        console.log("🔁 Resetting game state and navigating back to select-mode");
        dispatch(resetPlayer());
        dispatch(resetGame());
        dispatch(resetConfig());
        window.location.href = "/select-mode";
    };

    const handleConfirm = () => {
        if (playerId) {
            sendMessage("/config-confirmed", { playerId });
        }
    };

    const handleNext = () => {
        const sanitizedConfig = {
            ...config,
            playerId,
        };
        sendMessage("/config-update", sanitizedConfig);
        sendMessage("/navigate", "next");
    };

    return (
        <main className="flex flex-col items-center justify-center min-h-screen bg-cover bg-center w-full h-full p-8"
              style={{backgroundImage: "url('/image/config.png')"}}>

            <div className="bg-white p-8 rounded-lg shadow-lg w-full max-w-lg mt-16 space-y-4 space-x-5">
                <h1 className="text-xl font-bold text-center text-black">Set Up Your Game Configuration</h1>
                <p className="text-center text-gray-600">Players Connected: {players} / 1</p>
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
            </div>

            {/*<div className='flex justify-between mt-4'>*/}
            {/*    <div className='flex items-center gap-2'>*/}
            {/*        <div*/}
            {/*            className={`w-4 h-4 border-2 rounded ${youConfirmed ? 'bg-green-500 border-green-700' : 'bg-white'}`}/>*/}
            {/*        <span className='text-sm text-black'>You</span>*/}
            {/*    </div>*/}
            {/*</div>*/}

            {error && <p className="text-red-500 text-center font-bold">{error}</p>}

            <div className="mt-4 flex justify-end">
                <button
                    onClick={handleConfirm}
                    className="mt-4 bg-blue-500 text-white py-2 px-10 rounded hover:bg-blue-700 transition"
                >
                    Confirm Config
                </button>
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

            {/*<div*/}
            {/*    onClick={youConfirmed ? handleNext : undefined}*/}
            {/*    className={`absolute cursor-pointer bottom-10 right-20 transition-opacity ${*/}
            {/*        youConfirmed? "hover:opacity-75" : "opacity-50 cursor-not-allowed"*/}
            {/*    }`}*/}
            {/*>*/}
            <div
                onClick={handleNext}
                className="absolute cursor-pointer bottom-10 right-20"
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