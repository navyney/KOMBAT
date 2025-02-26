'use client';

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

export default function WaitingRoom() {
    const [playerCount, setPlayerCount] = useState(1);
    const router = useRouter();

    useEffect(() => {
        // จำลอง WebSocket หรือ API ที่รับจำนวนผู้เล่นปัจจุบัน
        const interval = setInterval(() => {
            // จำลองการอัปเดตจำนวนผู้เล่น (ให้แก้ไขเป็น WebSocket หรือ API จริง)
            setPlayerCount(prev => (prev < 2 ? prev + 1 : 2));
        }, 2000);

        return () => clearInterval(interval);
    }, []);

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-orange-100 w-full h-full">
            <h1 className="text-2xl font-bold mb-4">Waiting Room</h1>
            <p className="text-lg mb-4">Players: {playerCount}/2</p>
            <button
                onClick={() => router.push('/Config-set-up')}
                disabled={playerCount !== 2}
                className={`px-6 py-3 rounded-lg text-white font-semibold transition ${
                    playerCount === 2 ? "bg-orange-600 hover : hover:opacity-75 transition-opacity" : "bg-gray-400 cursor-not-allowed"
                }`}
            >
                Set-Up
            </button>
        </div>
    );
}
